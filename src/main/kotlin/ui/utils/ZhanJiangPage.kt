package ui.utils

import VSpace
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import button

@Composable
fun ZhanJiangPage() {

    val killNum = remember { mutableStateOf("") }
    val pureNum = remember { mutableStateOf("") }
    val pureReduce = remember { mutableStateOf("") }
    val attackNum = remember { mutableStateOf("30000") }

    var damage by remember { mutableStateOf("") }

    Column {

        Text("最终伤害:$damage")

        RowEdit("基础攻击", attackNum)

        RowEdit("杀兵数", killNum)
        VSpace(12)

        RowEdit("纯粹剩余", pureNum)
        VSpace(12)

        RowEdit("伤害减免", pureReduce)

        button("计算") {

            var kill = killNum.value.toIntOrNull() ?: 0
            if (kill == 0) {
                if (killNum.value.contains("x")) {
                    val num2 = killNum.value.split("x")
                    kill = num2[0].toInt() * num2[1].toInt()
                }
            }
            val pure = pureNum.value.toFloatOrNull() ?: 0f
            val pureReduce = pureReduce.value.toFloatOrNull() ?: 0f

            val attackUpRate = 0.05

            val baseAtt = attackNum.value.toFloatOrNull() ?: 0f

            val allAttcak = baseAtt * (1 + attackUpRate * kill) * pure.coerceAtLeast(0.1f) * (1 + kill * 0.02 - pureReduce).coerceAtLeast(0.1)

            damage = allAttcak.toInt().toString()
        }

    }


}

@Composable
fun RowEdit(title: String, value: MutableState<String>, hint: String = "") {
    Row {
        Text(title)
        OutlinedTextField(value.value, {
            value.value = it
        }, placeholder = { Text(hint) })
    }
}