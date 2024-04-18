package av.ponomarev.cows1

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.time.Duration
import kotlin.random.Random
import kotlin.Any


class MainActivity : AppCompatActivity() {

    private val input = listOf(
        listOf(R.id.editText11, R.id.editText12, R.id.editText13, R.id.editText14),
        listOf(R.id.editText21, R.id.editText22, R.id.editText23, R.id.editText24),
        listOf(R.id.editText31, R.id.editText32, R.id.editText33, R.id.editText34),
        listOf(R.id.editText41, R.id.editText42, R.id.editText43, R.id.editText44),
        listOf(R.id.editText51, R.id.editText52, R.id.editText53, R.id.editText54)
    )

    private var secretNumber = ""

    private var currentIt = 0

    private lateinit var checkAns  : Button
    private lateinit var saveState : Button
    private lateinit var newGame   : Button
    private lateinit var loadState : Button
    private lateinit var about     : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkAns  = findViewById(R.id.button1)
        checkAns.setOnClickListener {
            onCheckAnsClick()
        }

        saveState = findViewById(R.id.button2)
        saveState.setOnClickListener {
            onSaveStateClick()
        }

        newGame = findViewById(R.id.buttonNew)
        newGame.setOnClickListener {
            onGameNewClick()
        }

        loadState = findViewById(R.id.button3)
        loadState.setOnClickListener {
            onLoadStateClick()
        }

        about     = findViewById(R.id.button4)
        about.setOnClickListener {
            onAboutClick()
        }

        restart()

        Toast.makeText(this, secretNumber, Toast.LENGTH_LONG).show()
    }

    private fun onCheckAnsClick() {
        var suggestionsCount = 0

        for (i in 0..3){
            val currentEditText = findViewById<EditText>(input[currentIt][i])
            val suggestedNumber = currentEditText.text.toString().toCharArray()[0]

            if (suggestedNumber == secretNumber[i]) {
                currentEditText.setBackgroundColor(Color.GREEN)
                suggestionsCount++
            } else if (secretNumber.contains(suggestedNumber)) {
                currentEditText.setBackgroundColor(Color.rgb(255, 165, 0))
            } else {
                currentEditText.setBackgroundColor(Color.GRAY)
            }
        }

        if (suggestionsCount == 4)
        {
            onWin()
        }
        else if (currentIt == 4)
        {
            onDefeat()
        }
        else
        {
            currentIt++
            setEnabled(currentIt)
        }
    }

    private fun onSaveStateClick()
    {
        val file = File(filesDir, "SavedState.txt")

        var nums = ""

        for (i in 0..currentIt)
        {
            for (j in 0..3)
            {
                nums += findViewById<EditText>(input[i][j]).text.toString()
            }
        }

        val state = currentIt.toString() + secretNumber + nums

        Toast.makeText(this, state.replace(" ", ""), Toast.LENGTH_SHORT).show()

        file.writeText(state.replace(" ", ""))
    }

    private fun onGameNewClick()
    {
        restart()
    }

    private fun onLoadStateClick()
    {
        val file = File(filesDir, "SavedState.txt")
        val state = file.readText()

        secretNumber = (state[1].toString() + state[2].toString() + state[3].toString() + state[4].toString())

        Toast.makeText(this, state, Toast.LENGTH_SHORT).show()

        currentIt = state[0].toString().toInt()

        var currentChar = 5
        for (i in 0..< currentIt)
        {
            for (j in 0..3)
            {
                findViewById<EditText>(input[i][j]).setText(state[currentChar].toString())
                currentChar++
            }

            for (j in 0..3){
                val currentEditText = findViewById<EditText>(input[i][j])
                val suggestedNumber = currentEditText.text.toString().toCharArray()[0]

                if (suggestedNumber == secretNumber[j]) {
                    currentEditText.setBackgroundColor(Color.GREEN)
                } else if (secretNumber.contains(suggestedNumber)) {
                    currentEditText.setBackgroundColor(Color.rgb(255, 165, 0))
                } else {
                    currentEditText.setBackgroundColor(Color.GRAY)
                }
            }
        }
        setEnabled(currentIt)
    }

    private fun onAboutClick()
    {
        val message = "Вам необходимо ввести 4-значное число в поле ввода, нажать кнопку" +
                " \"Проверить\", затем поля окрасятся в разные цвета: \n 1. Серый - цифра отсутствует. \n" +
                " 2. Желтый - цифра есть, но она не на своём месте. \n" +
                " 3. Зеленый - цифра есть и стоит на своём месте. \n" +
                "Ваша задача - отгадать 4-значеное число, загаданное компьютером"

        showDialog("Об игре", message)
    }

    private fun onWin()
    {
        val message = "Поздравляем! Вы смогли отгадать число за количество попыток: ${currentIt + 1}!"
        showDialog("Победа!", message, "Новая игра");
        restart()
    }

    private fun onDefeat()
    {
        val message = "Вы проиграли! Вы можете попробовать снова!"
        showDialog("Поражение!", message, "Новая игра");
        restart()
    }

    private fun setEnabled(currentLine : Int){
        for (i in input.indices)
            for (j in input[i].indices)
                findViewById<EditText>(input[i][j]).isEnabled = false

        for (i in 0..3){
            findViewById<EditText>(input[currentLine][i]).isEnabled = true
            findViewById<EditText>(input[currentLine][i]).setBackgroundColor(Color.rgb(160, 149, 118))
        }
    }

    private fun restart()
    {
        for (i in input.indices)
        {
            for (j in input[i].indices)
            {
                findViewById<EditText>(input[i][j]).setText("")
                findViewById<EditText>(input[i][j]).setBackgroundColor(Color.rgb(137, 129, 118))
            }
        }

        setEnabled(0);

        currentIt = 0
        makeNewSecretNum()
    }

    private fun showDialog(title : String, message: String, buttonName: String = "OK")
    {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonName) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }


    private fun makeNewSecretNum()
    {
        val digits = ('0'..'9').shuffled(Random)
        secretNumber = digits.subList(0, 4).joinToString("")
    }
}