package ru.kireev.mir.volunteerlizaalert.utils;

public class DeparturesMessageHandler {

    public static String getInforgPhoneNumber(String message) {
        //переводим строку в ниэний регистр, разбиваем на две части по слову инфорг, берем второй элемент массива
        //разбиваем его на две части по закрывающей скобке, берем второй элемент, обрезаем лишние пробелы
        return message.toLowerCase().split("инфорг")[1].split("\\)")[1].split("тема")[0].trim();
    }

    public static String getDepartureMessageTopic(String message) {
        return message.split("СБОР")[0].trim();
    }

    public static String getDepartureTime(String message) {
        return message.split("СБОР")[1].split("МЕСТО")[0].trim();
    }

    public static String getDeparturePlace(String message) {
        return message.split("МЕСТО СБОРА")[1].split("Координатор")[0].trim();
    }

    public static String getCoordinator(String message) {
        return message.split("Координатор")[1].split("Инфорг")[0].trim();
    }

    public static String getInforg(String message) {
        return message.split("Инфорг")[1].split("\\d")[0].trim();
    }

    public static String getForumTopic(String message) {
        return message.split("Тема на форуме")[1].trim();
    }
}
