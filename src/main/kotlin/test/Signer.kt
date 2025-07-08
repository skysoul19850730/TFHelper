package test

import org.apache.commons.codec.binary.Hex
import org.apache.commons.lang3.StringUtils
import org.apache.http.Consts
import org.apache.http.NameValuePair
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.utils.URIBuilder
import org.apache.http.client.utils.URLEncodedUtils
import org.apache.http.util.EntityUtils
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class Signer {
    @Throws(Exception::class)
    fun sign(request: HttpUriRequest, credentials: WindowTest.Credentials) {
        if ("" == request.uri.getPath()) {
            val uri = request.uri
            val builder = URIBuilder(uri)
            builder.setPath("/")
            if (request is HttpRequestBase) {
                request.uri = builder.build()
            } else {
                throw NullPointerException("Path can't be empty. If you don't have path for request, please use a '/' instead.")
            }
        }

        // common headers
        request.setHeader("Host", request.uri.host)
        if (request.getHeaders("Content-Type") == null) {
            request.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
        }
        val formatDate = currentFormatDate
        request.setHeader("X-Date", formatDate)
        val meta = MetaData()
        meta.algorithm = "HMAC-SHA256"
        meta.service = credentials.service
        meta.region = credentials.region
        meta.date = toDate(formatDate)

        // step 1
        val hashedCanonReq = hashedCanonicalRequestV4(request, meta)
        meta.credentialScope = StringUtils
            .join(arrayOf(meta.date, meta.region, meta.service, "request"), "/")
        // step 2
        val stringToSign =
            StringUtils.join(arrayOf(meta.algorithm, formatDate, meta.credentialScope, hashedCanonReq), "\n")

        // step 3
        val signingKey = genSigningSecretKeyV4(credentials.sk, meta.date!!, meta.region!!, meta.service!!)
        val signature = Hex.encodeHexString(Utils.hmacSHA256(signingKey, stringToSign))
        request.setHeader("Authorization", buildAuthHeaderV4(signature, meta, credentials))
    }

    @Throws(Exception::class)
    private fun hashedCanonicalRequestV4(request: HttpUriRequest, meta: MetaData): String {
        var body: ByteArray? = ByteArray(0)
        if (request is HttpEntityEnclosingRequestBase) {
            val entity = request.entity
            if (entity != null) {
                body = EntityUtils.toByteArray(entity)
            }
        }
        val bodyHash = Utils.hashSHA256(body)
        request.setHeader("X-Content-Sha256", bodyHash)
        val signedHeaders: MutableList<String> = ArrayList()
        for (header in request.allHeaders) {
            val headerName = header.name
            if (H_INCLUDE.contains(headerName) || headerName.startsWith("X-")) {
                signedHeaders.add(headerName.lowercase(Locale.getDefault()))
            }
        }
        signedHeaders.sort()
        val signedHeadersToSignStr = StringBuilder()
        for (h in signedHeaders) {
            var value = request.getFirstHeader(h).value.trim { it <= ' ' }
            if (h == "host") {
                if (value.contains(":")) {
                    val split = value.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val port = split[1]
                    if (port == "80" || port == "443") {
                        value = split[0]
                    }
                }
            }
            signedHeadersToSignStr.append(h).append(":").append(value).append("\n")
        }
        meta.signedHeaders = StringUtils.join(signedHeaders, ";")
        val canonicalRequest = StringUtils.join(
            arrayOf(
                request.method,
                normUri(request.uri.getPath()),
                normQuery(URLEncodedUtils.parse(request.uri.rawQuery, Consts.UTF_8, '&')),
                signedHeadersToSignStr.toString(),
                meta.signedHeaders, bodyHash
            ),
            "\n"
        )
        return Utils.hashSHA256(canonicalRequest.toByteArray())
    }

    @Throws(Exception::class)
    private fun genSigningSecretKeyV4(secretKey: String, date: String, region: String, service: String): ByteArray {
        val kDate = Utils.hmacSHA256(secretKey.toByteArray(), date)
        val kRegion = Utils.hmacSHA256(kDate, region)
        val kService = Utils.hmacSHA256(kRegion, service)
        return Utils.hmacSHA256(kService, "request")
    }

    private fun buildAuthHeaderV4(signature: String, meta: MetaData, credentials: WindowTest.Credentials): String {
        val credential = credentials.ak + "/" + meta.credentialScope
        return meta.algorithm +
                " Credential=" + credential +
                ", SignedHeaders=" + meta.signedHeaders +
                ", Signature=" + signature
    }

    private val currentFormatDate: String
        private get() {
            val df: DateFormat = SimpleDateFormat(TIME_FORMAT_V4)
            df.timeZone = tz
            return df.format(Date())
        }

    private fun toDate(timestamp: String): String {
        return timestamp.substring(0, 8)
    }

    private fun normUri(path: String): String {
        return URLEncoder.encode(path).replace("%2F", "/").replace("+", "%20")
    }

    private fun normQuery(params: List<NameValuePair>): String {
        Collections.sort(params, NameValueComparator.INSTANCE)
        val query = URLEncodedUtils.format(params, Consts.UTF_8)
        return query.replace("+", "%20")
    }

    companion object {
        private val tz = TimeZone.getTimeZone("UTC")
        private val H_INCLUDE: MutableSet<String> = HashSet()
        private const val TIME_FORMAT_V4 = "yyyyMMdd'T'HHmmss'Z'"

        init {
            H_INCLUDE.add("Content-Type")
            H_INCLUDE.add("Content-Md5")
            H_INCLUDE.add("Host")
        }
    }
}
