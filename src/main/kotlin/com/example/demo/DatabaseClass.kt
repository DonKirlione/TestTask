package com.example.demo

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.time.LocalDate
import javax.swing.JOptionPane

//Класс для работы с базой
class DatabaseClass {
    // Связываемся с базой данных SQLite
    private val connection: Connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/notes.db")
    private val statement: Statement = connection.createStatement()

    //Обновление списка
    fun refreshListView(start: Boolean?): ObservableList<Note>{
        val list= FXCollections.observableArrayList<Note>()
        //Запрос
        val resultSet: ResultSet = statement.executeQuery("SELECT * FROM notes ORDER BY date DESC")
        //Обработка результатов запроса
        while (resultSet.next()) {
            val id = resultSet.getInt("id")
            val date = LocalDate.parse(resultSet.getString("date"))
            val title = resultSet.getString("title")
            val text = resultSet.getString("text")
            //Пополняем список
            list.add(Note(id, date, title, text))

            //Если первый запуск
            if (start != null) {
                //Сверяем дату
                if (date == LocalDate.now()) {
                    //Выбрасываем уведомление
                    JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE)
                }
            }


        }
        //Закрываем соединения
        resultSet.close()
        statement.close()
        return list
    }

    //Изменение заметки
    fun updateNote(note:Note){
        val answer =
            statement.executeUpdate("UPDATE notes SET date = '${note.date}', title = '${note.title}', text = '${note.text}' WHERE id = ${note.id}")
        statement.close()
    }

    //Добавление заметки
    fun addNote(note: Note){
        val answer =
            statement.executeUpdate("INSERT INTO notes (date, title, text) VALUES ('${note.date}','${note.title}','${note.text}')")
        statement.close()
    }

    //Удаление заметки
    fun deleteNote(id: Int){
        val answer = statement.executeUpdate("DELETE FROM notes WHERE id = $id")
        statement.close()
    }
}