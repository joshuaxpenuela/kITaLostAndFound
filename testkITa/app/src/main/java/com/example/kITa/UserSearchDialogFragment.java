package com.example.kITa;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserSearchDialogFragment extends DialogFragment {

    private EditText chatUserSearch;
    private RecyclerView chatUserSelectRecycleView;
    private UserSearchAdapter userSearchAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_chat_user_select, container, false);

        chatUserSearch = view.findViewById(R.id.chatUserSearch);
        chatUserSelectRecycleView = view.findViewById(R.id.chatUserSelectRecycleView);

        chatUserSelectRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        userSearchAdapter = new UserSearchAdapter();
        chatUserSelectRecycleView.setAdapter(userSearchAdapter);

        chatUserSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        return view;
    }

    private void searchUsers(String query) {
        ApiClient.searchUsers(query, new ApiCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> users) {
                userSearchAdapter.setUsers(users);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error searching users: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
