import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ui.zhandou.data.ZhanDouModel

@Composable
fun zdBasePage(model: ZhanDouModel) {
    Row {
        if (model.subModels.size > 0) {
            LazyColumn(Modifier.padding(0.dp, 50.dp)) {
                itemsIndexed(model.subModels) { _, item ->
                    subMenu(model, item)
                }
            }
        }
        Column {
            Row(Modifier.fillMaxWidth().weight(1f).padding(20.dp)) {
                Box {
                    model.mainPage(this)
                }
            }
        }

    }
}



@Composable
fun subMenu(model: ZhanDouModel, s: String) {
    Button(
        {
            model.subSelected.value = s
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = if (model.subSelected.value == s) Color.Gray else Color.LightGray)
    ) {
        Text(s, color = Color.Black)
    }

}