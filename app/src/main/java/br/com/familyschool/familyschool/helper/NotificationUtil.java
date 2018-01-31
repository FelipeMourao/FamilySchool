package br.com.familyschool.familyschool.helper;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;

import br.com.familyschool.familyschool.R;

public class NotificationUtil {
    //Cria a PendingItent para abrir a activity da intent
    private static PendingIntent getPendingIntent(Context context, Intent intent, int id){
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        //linha que mantém a activity pai na pilha de activities
        stackBuilder.addParentStack(intent.getComponent());
        //Configura a intent que vai abrir ao clicar na notificação
        stackBuilder.addNextIntent(intent);
        //Cria a PendingIntent e atualiza caso exista uma com mesmo id
        PendingIntent p = stackBuilder.getPendingIntent(id, PendingIntent.FLAG_UPDATE_CURRENT);
        return p;
    }

    public static void create(Context context, Intent intent, String contentTitle, String contentText, int id){
        //Cria a PendingIntent (contém a intent original)
        PendingIntent p = getPendingIntent(context,intent,id);
        //Cria a notificação
        NotificationCompat.Builder b = new NotificationCompat.Builder(context);
        b.setDefaults(Notification.DEFAULT_ALL); //Ativa configuraç~es padrão
        b.setSmallIcon(R.drawable.ic_familiy);//Icone
        b.setContentTitle(contentTitle); //Titulo
        b.setContentText(contentText); //Mensagem
        b.setContentIntent(p); //Intent que srá chamanda ao clicar na notificação
        b.setAutoCancel(true); //AutoCancela a notificação ao clicar nela
        b.setFullScreenIntent(p,false);//heads-up notification
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        //Neste caso a notificação será cancelada automaticamente quando o usuario clicar
        //Mas o id serve para cancelá-la manualmente se necessário
        nm.notify(id,b.build());
    }


    public static void cancell(Context context, int id){
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancel(id);
    }

    public static void cancellAll(Context context){
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancelAll();
    }
}
