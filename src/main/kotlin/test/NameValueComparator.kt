package test

import org.apache.http.NameValuePair

enum class NameValueComparator : Comparator<NameValuePair> {
    INSTANCE;

    override fun compare(o1: NameValuePair, o2: NameValuePair): Int {
        return if (o1.name == null) {
            if (o2.name == null) 0 else if (NULL_FIRST) -1 else 1
        } else if (o2.name == null) {
            if (NULL_FIRST) 1 else -1
        } else {
            o1.name.compareTo(o2.name)
        }
    }

    companion object {
        private const val NULL_FIRST = false
    }
}
