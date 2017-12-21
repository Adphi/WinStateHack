package fr.wcs.winstatehack;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import fr.wcs.winstatehack.Controllers.FirebaseController;
import fr.wcs.winstatehack.Models.UserModel;
import fr.wcs.winstatehack.UI.FriendsAdapter;

public class FriendsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        FirebaseController firebaseController = FirebaseController.getInstance();

        ArrayList<UserModel> users = firebaseController.getUsers();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewFriends);
        FriendsAdapter friendsAdapter = new FriendsAdapter(users);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(friendsAdapter);
        firebaseController.setUsersListener(user -> friendsAdapter.notifyDataSetChanged());
    }
}
