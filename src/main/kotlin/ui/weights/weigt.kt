package ui.weights

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ui.colorMenuRowBg

@Composable
fun MCheckBox(text: String?, state: MutableState<Boolean>) {
    Row(Modifier.clickable {
        state.value = !state.value
    }) {
        Checkbox(state.value, null)
        Text(text ?: "", Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
fun MRadioBUtton(text: String?, mId: Int, state: MutableState<Int>) {
    Row(Modifier.clickable {
        state.value = mId
    }) {
        RadioButton(mId == state.value, null)
        Text(text ?: "", Modifier.align(Alignment.CenterVertically))
    }
}

@Composable
fun <T> MRaidoGroup(selected: MutableState<T>, list: List<T>) {
    Row {
        list.forEach {
            Row(Modifier.clickable {
                selected.value = it
            }) {
                RadioButton(it == selected.value, null)
                Text(it.toString() ?: "", Modifier.align(Alignment.CenterVertically), color = Color.DarkGray)
            }
        }
    }
}

@Composable
fun button(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = {
        onClick.invoke()
    }, modifier) {
        Text(text)
    }
}

@Composable
fun button(text: State<String>, onClick: () -> Unit) {
    Button(onClick = {
        onClick.invoke()
    }) {
        Text(text.value)
    }
}