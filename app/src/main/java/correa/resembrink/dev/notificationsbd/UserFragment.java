package correa.resembrink.dev.notificationsbd;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {

    private RecyclerView mUserListView;

    private List<Users> usersList;

    private UserRecyclerAdapter userRecyclerAdapter;

    private FirebaseFirestore mFirestore;




    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_user, container, false);
        mFirestore=FirebaseFirestore.getInstance();

        mUserListView= view.findViewById(R.id.user_list_view);


        usersList=new ArrayList<>();



        userRecyclerAdapter= new UserRecyclerAdapter(container.getContext(), usersList);

        mUserListView.setHasFixedSize(true);
        mUserListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mUserListView.setAdapter(userRecyclerAdapter);


        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();

        usersList.clear();

//getActivity().getParent()

        mFirestore.collection("Users").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots,  FirebaseFirestoreException e) {

                for(DocumentChange doc: documentSnapshots.getDocumentChanges())
                {
                    if(doc.getType() == DocumentChange.Type.ADDED)
                    {

                        String  user_id=doc.getDocument().getId();
                        Users users= doc.getDocument().toObject(Users.class).withId(user_id);
                        usersList.add(users);

                        userRecyclerAdapter.notifyDataSetChanged();

                    }
                }

            }
        });
    }
}
