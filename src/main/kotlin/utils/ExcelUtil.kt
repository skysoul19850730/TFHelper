package utils

import data.Config
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

class ExcelUtil {

    val s = "患者姓名,住院号,性别,年龄(岁),身高(cm),体重(kg),BMI,BSA(m2),吸烟指数（年支）,肿瘤病史,肺部基础病,病灶位置（上叶/下叶）,CT（实变）,纵隔/肺门肿大淋巴结,胸水,胸膜下病灶,非胸膜下病灶,穿刺体位（仰卧/侧卧/俯卧）,穿刺位置（前、后、侧）,穿刺针与皮肤表面接触点到壁层胸膜之间距离（mm）,穿刺层面（长径与短径乘积）,穿刺针经过肺组织长度（mm）,穿刺角度,穿过胸膜次数,是否过叶间裂,穿刺针刺入时间（进胸膜至退针min）,气胸（程度）,出血,其他并发症,病理结果,C反应蛋白（0-10mg/L),红细胞计数(3.8-5.1×10^12/L),血红蛋白(115-150g/L),红细胞容积(35-45%),平均红细胞体积(82-100fL),平均红细胞血红蛋白含量(27-34pg）,平均红细胞血红蛋白浓度（316-354g/L）,红细胞体积分布宽度(SD)（37-50fL）,红细胞体积分布宽度(CV)：无参考值,白细胞（3.5-9.5×10^9/L）,中性粒细胞百分比（40-75%）,淋巴细胞百分比（20-50%）,单核细胞百分比（3-10%）,嗜酸性粒细胞百分比（0.4-8.0%）,嗜碱性粒细胞百分比（0-1%）,中性粒细胞绝对值（1.8-6.3×10^9/L）,淋巴细胞绝对值（1.1-3.2×10^9/L）,单核细胞绝对值（0.1-0.6×10^9/L）,嗜酸性粒细胞绝对值（0.02-0.52×10^9/L）,嗜碱性粒细胞绝对值（0.00-0.06×10^9/L）,血小板计数(125-350×10^9/L),血小板容积（%）,平均血小板体积(9-13fL),血小板体积分布宽度(9-17fL),丙氨酸氨基转移酶（7-40U/L）,天门冬氨酸氨基转移酶（13-35U/L）,天冬氨酸氨基转移酶同工酶（0-15U/L）,AST/ALT比值,碱性磷酸酶（50-135U/L）,前白蛋白（0.18-0.35g/L）,白蛋白(溴甲酚绿法)（40-55g/L）,总蛋白（65-85g/L）,球蛋白（20-40g/L）,白球比（1.2-2.4）,ALB白蛋白（55.8-66.1%）,总胆红素（0-21.0μmol/L）,直接胆红素（0-8.0μmol/L）,γ-谷氨酰基转移酶（7-45U/L）,乳酸脱氢酶（120-250U/L）,乳酸脱氢酶同工酶（15-65U/L）,肌酐(酶法)（41-81μmol/L）,尿素（3.1-8.8mmol/L）,钾（3.5-5.3mmol/L）,钠（137-147mmol/L）,氯（99-110mmol/L）,总二氧化碳（22-30mmol/L） ,凝血酶原时间测定(时间)（9.6-13.7sec）,凝血酶原时间测定(INR)（0.73-1.27INR）,活化部分凝血活酶时间测定(22.7-36.4sec),血浆纤维蛋白原测定(2.0-4.0g/L),凝血酶时间测定(14-21sec),纤维蛋白(原)降解产物(<5ug/ml),D-二聚体测定(0-500ng/mL(FEU)),基础疾病1,基础疾病2,基础疾病3"

    var titles = s.split(",")

    val excelName = "肺穿刺数据收集.xls"

    fun mtest(){
        val work = HSSFWorkbook(File(Config.caiji_main_path,excelName).inputStream())
        val sheet = work.getSheet("Sheet1")
        val text = sheet.getRow(0).joinToString(",")
        println(text)
    }

    fun test(){
        val users = File(Config.caiji_main_path,"users").listFiles()
        val work = HSSFWorkbook(File(Config.caiji_main_path,excelName).inputStream())
        val sheet = work.getSheet("Sheet1")

        val text = sheet.getRow(0).joinToString(",")
        titles = text.split(",")

        val modelRow = sheet.getRow(2)

        users.forEach {
            writeOneUser(it,sheet,modelRow)
        }

        val out = File(Config.caiji_main_path,excelName).outputStream()

        work.write(out)
        out.close()

    }

    private fun writeOneUser(user:File,sheet:Sheet,modelRow:Row){


        var row: Row? = sheet.firstOrNull {
            it.getCell(0)?.stringCellValue == user.nameWithoutExtension
        }
        if(row==null){
            row = sheet.createRow(sheet.lastRowNum+1).apply {
                createCell(0).setCellValue(user.nameWithoutExtension)
            }
        }


        val text = user.readText()
        val lines = text.split("\n")
        for(line2 in lines){
            var line =line2.replace("\\s+".toRegex()," ")
            line = line.replace("\\s*-\\s*".toRegex(), "-")
            val kv = line.split(" ").filter { it.isNotBlank()&&it.isNotEmpty() }


            if(kv.size>=2){
                val key = kv[0].replace("★","")
                val value = kv[1]

                val index = titles.indexOfFirst {
                    it.startsWith(key)
                }
                if(index>0){
                    row?.getOrCreateCell(index,modelRow)?.apply {
                        val cellType = modelRow?.getCell(index)?.cellType
                        if(cellType==CellType.STRING) {
                            setCellValue(value)
                        }else if(cellType==CellType.NUMERIC){
                            setCellValue(value.toDouble())
                        }
                    }
                }
            }
        }


    }

    fun Row.getOrCreateCell(index:Int,modelRow: Row?=null):Cell{
        var cell = getCell(index)
        if(cell==null){
            cell = createCell(index)
            cell.cellType = modelRow?.getCell(index)?.cellType
            cell!!.cellStyle =   modelRow?.getCell(cell.columnIndex)?.cellStyle?.let { style ->
                    val newStyle = sheet.workbook.createCellStyle()
                    newStyle.cloneStyleFrom(style)
                    newStyle
                }

        }
        return cell
    }


}