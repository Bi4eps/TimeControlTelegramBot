import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;

public class Bot extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {                     //Main function in this class
        if (update.hasMessage() && update.getMessage().hasText()) {
            //sendText("We got your message", update.getMessage().getChatId().toString());
            boolean avail_chatID_in_db = false;
            int number_in_db = 0;
            String text = "";
            for (int i = 0; i < 3; i++) {
                if (allChatsIDs[i] != null) {
                    if (allChatsIDs[i].equals(update.getMessage().getChatId().toString())) {
                        //sendText("OMG, we have your ID", allChatsIDs[i]);
                        avail_chatID_in_db = true;
                        number_in_db = i;
                        System.out.println(number_in_db);
                        break;
                    }
                }
            }
            switch (update.getMessage().getText()) {
                case "/start":
                    if (!avail_chatID_in_db){
                        for (int i = 0; i < 3; i++) {
                            System.out.println(allChatsIDs[i]);
                            if (allChatsIDs[i] == null) {
                                allChatsIDs[i] = update.getMessage().getChatId().toString();
                                System.out.println(allChatsIDs[i]);
                                sendText("Bot started his work", update.getMessage().getChatId().toString());
                                break;
                            }
                            if (i == 2){
                                sendText("Please wait, too many people use this bot now", update.getMessage().getChatId().toString());
                            }
                        }
                    } else {
                        sendText("Bot already started his work earlier", allChatsIDs[number_in_db]);
                    }
                    break;
                case "/show_result":
                    if (!avail_chatID_in_db){
                        sendText("Bot don't started his work", update.getMessage().getChatId().toString());
                        break;
                    }
                    var message = new SendMessage();
                    message.setChatId(update.getMessage().getChatId().toString());
                    var murkup = new ReplyKeyboardMarkup();
                    var keyboardrow = new ArrayList<KeyboardRow>();
                    KeyboardRow row1 = new KeyboardRow();
                    row1.add("/finish");
                    keyboardrow.add(row1);
                    murkup.setKeyboard(keyboardrow);
                    murkup.setResizeKeyboard(true);
                    message.setReplyMarkup(murkup);
                    for (int i = 0; i < 10; i++) {
                        if (affairs [number_in_db] [i] == null){
                            try {
                                execute(message);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                        if (affairs [number_in_db] [i + 1] == null){
                            String.valueOf(time(update.getMessage().getDate() - dates[number_in_db] [i]));
                            text += String.valueOf(time(update.getMessage().getDate() - dates[number_in_db] [i])) + " - "
                                    + affairs[number_in_db] [i] + "\n";
                            message.setText(text);
                        }else {
                            text += String.valueOf(time(dates[number_in_db] [i + 1] - dates[number_in_db] [i])) + " - "
                                    + affairs[number_in_db] [i] + "\n";
                            message.setText(text);
                        }
                    }
                    break;
                case "/finish":
                    if (avail_chatID_in_db){
                        for (int i = 0; i < 3; i++) {
                            if (update.getMessage().getChatId().toString().equals(allChatsIDs[i])){
                                allChatsIDs[i] = null;
                                System.out.println( i + " is clear");
                            }
                        }
                        sendText("Bot finished his work", update.getMessage().getChatId().toString());
                    } else
                        sendText("Bot don't started his work", update.getMessage().getChatId().toString());
                    break;
                default:
                    if (!avail_chatID_in_db){
                        sendText("Bot don't started his work", update.getMessage().getChatId().toString());
                    }
                    for (int i = 0; i < 10; i++) {
                        if (affairs[number_in_db] [i] == null) {
                            affairs[number_in_db] [i] = update.getMessage().getText();
                            dates[number_in_db]  [i] = update.getMessage().getDate();
                            System.out.println("The db " + number_in_db + " 0 " + i + " equals - " + affairs[number_in_db] [i]);
                            System.out.println("Date is - " + dates[number_in_db]  [i]);
                            break;
                        }
                    }
                    break;
            }
        }
    }
    public void sendText(String text, String soutID){
        var message = new SendMessage();
        message.setChatId(soutID);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public String [] allChatsIDs = {null, null, null};

    public String[] [] affairs = new String[3] [10];       //1st number is user code
    public int[] [] dates = new int[3] [10];
    public static String time(int seconds){
        if (seconds < 60){
            return seconds + "s";
        } else if (seconds >= 3600){
            int minSec = seconds % 3600;  //minutes + seconds
            int hours = (seconds - minSec) / 3600;
            int clear_seconds = minSec % 60;
            int minutes = (minSec - clear_seconds) / 60;
            String result = hours + " hours";
            if (minutes != 0){
                result = result + " " + minutes + "min";
            }
            if (clear_seconds != 0){
                result = result + " " + clear_seconds + "s";
            }
            return result;
        } else {
            int clear_seconds = seconds % 60;
            int minutes = (seconds - clear_seconds) / 60;
            if (clear_seconds == 0){
                return minutes + "min";
            } else {
                return minutes + "min " + clear_seconds + "s";
            }
        }
    }            //Converts seconds to time format

    @Override
    public String getBotUsername() { return "MinigameStrategyBot"; }
    @Override
    public String getBotToken() { return "5041321205:AAGltSer_idiIy5bvQhrk-8P24HyszN3hZU"; }
}