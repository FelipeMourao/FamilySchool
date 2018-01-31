package br.com.familyschool.familyschool.config;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public final class ConfiguracaoFirebase {

    private static DatabaseReference referenceDatabase;
    private static FirebaseAuth autenticacao;
    private static FirebaseStorage storageReference;

    public static DatabaseReference getFireBase(){

        if (referenceDatabase == null) {
            referenceDatabase = FirebaseDatabase.getInstance().getReference();
        }

        return referenceDatabase;
    }

    public static FirebaseAuth getFirebaseAutenticacao(){
        if (autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }

        return autenticacao;
    }

    public static FirebaseStorage getStorageReference(){
        if (storageReference == null){
            storageReference = FirebaseStorage.getInstance();
        }
        return storageReference;
    }

}
