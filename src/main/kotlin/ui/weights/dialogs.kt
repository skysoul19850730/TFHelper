package ui.weights

import HSpace
import VSpace
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.PopupAlertDialogProvider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun showInputDialog(
    title: String,
    hint: String,
    showState: MutableState<Boolean>,
    onSuc: RowScope.(String) -> Boolean
) {
    if (showState.value) {
        var text = remember { mutableStateOf("") }
        with(PopupAlertDialogProvider) {
            AlertDialog({
                showState.value = false
            }) {
                Box(Modifier.clickable { }.width(IntrinsicSize.Min)) {
                    Column(
                        Modifier.heightIn(50.dp, 200.dp).background(Color.White)
                            .border(
                                1.dp, Color.Blue,
                                RoundedCornerShape(12.dp)
                            ).clip(RoundedCornerShape(12.dp))
                            .padding(20.dp)
                            .verticalScroll(
                                state = rememberScrollState()
                            )
                    ) {
                        Text(title)
                        VSpace(12)
                        OutlinedTextField(text.value, {
                            text.value = it
                        }, placeholder = { Text(hint) })
                        VSpace(12)
                        Row {

                            Spacer(Modifier.weight(1f, true))
                            button("取消") {
                                showState.value = false
                            }
                            HSpace(12)
                            button("确定") {
                                if (onSuc.invoke(this, text.value)) {
                                    showState.value = false
                                }

                            }

                        }

                    }
                }
            }
        }
    }
}