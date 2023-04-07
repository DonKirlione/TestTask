package com.example.demo

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.time.LocalDate




class HelloApplication : Application() {
   //Создаем пустую коллекцию для заметок
    private var notesList = FXCollections.observableArrayList<Note>()
    private val addButton = Button("Добавить заметку").apply {
        setOnAction { openAddNoteDialog() }
    }
    private val mainRoot = BorderPane()
    //Список
    private var notesListView = ListView<Note>().apply {
        items = notesList
        //Обработка ДаблКлика
        setOnMouseClicked { event ->
            if (event.clickCount == 2) {
                //Костыль
                val index = selectionModel.selectedIndex
                val selectedNote = notesList[index]
                openNoteInfoWindow(selectedNote)
            }
        }
    }
    private val database = DatabaseClass()

    override fun start(primaryStage: Stage) {
        refreshListView(true)
        val menuPanel = addButton
        val listPanel = BorderPane(notesListView)
        mainRoot.top = menuPanel
        mainRoot.center = listPanel
        val scene = Scene(mainRoot)
        primaryStage.scene = scene
        primaryStage.title = "Заметки"
        primaryStage.show()


    }

    //Обновление заметок, пробрасываем null если не первый вызов функции, любой boolean если первый
    private fun refreshListView(isStart: Boolean?){
        notesList.clear()
        notesList = database.refreshListView(isStart)
        notesListView.items = notesList
    }
    //Диалоговое окно для добавления
    private fun openAddNoteDialog() {
        val dialogStage = Stage()
        val dialogScene = noteDialogScene(dialogStage, null)
        dialogStage.scene = dialogScene
        dialogStage.title = "Добавление"
        dialogStage.show()
    }

    private fun noteDialogScene(dialogStage: Stage, note: Note?): Scene {
        val dateLabel = Label("Дата:")
        val titleLabel = Label("Заголовок:")
        val textLabel = Label("Текст:")
        var datePicker = DatePicker(LocalDate.now())
        var titleField = TextField()
        var textField = TextArea()
        var saveButton = Button("Добавить")
        var cancelButton = Button("Отмена")

        if (note != null) {
            datePicker = DatePicker(note.date)
            titleField = TextField(note.title)
            textField = TextArea(note.text)
            saveButton = Button("Изменить")
            cancelButton = Button("Удалить")
        }


        saveButton.setOnAction {
            val dateString = datePicker.value
            val title = titleField.text.trim()
            val text = textField.text.trim()
            if (note != null) {
                database.updateNote(Note(note.id, dateString, title, text))
            } else {
                database.addNote(Note(0, dateString, title, text))
            }
            refreshListView(null)
            dialogStage.close()
        }

        cancelButton.setOnAction {
            if (note != null) {
                database.deleteNote(note.id)
            }
            refreshListView(null)
            dialogStage.close()
        }

        val root = VBox()
        root.children.addAll(
            dateLabel,
            datePicker,
            titleLabel,
            titleField,
            textLabel,
            textField,
            saveButton,
            cancelButton
        )
        return Scene(root, 300.0, 250.0)
    }

    //Диалоговое окно для просмотра/Изменения
    private fun openNoteInfoWindow(note: Note) {
        val noteInfoStage = Stage()
        val noteInfoScene = noteDialogScene(noteInfoStage, note)
        noteInfoStage.scene = noteInfoScene
        noteInfoStage.title = "Изменение"
        noteInfoStage.show()
    }
}

//Заметка
class Note(val id: Int, val date: LocalDate, val title: String, val text: String) {

    override fun toString(): String {
        return "[$date]: $title"
    }
}