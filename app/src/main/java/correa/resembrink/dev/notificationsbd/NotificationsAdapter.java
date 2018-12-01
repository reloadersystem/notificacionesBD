package correa.resembrink.dev.notificationsbd;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notifications> mNotifList;

    private FirebaseFirestore firebaseFirestore;

    private Context context;

    public NotificationsAdapter(Context context, List<Notifications> mNotifList) {
        this.mNotifList = mNotifList;
        this.context = context;
    }


    @NonNull
    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_notification, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NotificationsAdapter.ViewHolder holder, int position) {

        firebaseFirestore= FirebaseFirestore.getInstance();
        String from_id= mNotifList.get(position).getFrom();

        holder.mNotifMessage.setText(mNotifList.get(position).getMessage());

        firebaseFirestore.collection("Users").document(from_id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name= documentSnapshot.getString("name");
                String image= documentSnapshot.getString("image");

                holder.mNotifName.setText(name);

                RequestOptions requestOptions= new RequestOptions();
                requestOptions.placeholder(R.mipmap.ic_launcher_round);

                Glide.with(context).setDefaultRequestOptions(requestOptions).load(image).into(holder.mNotifImage);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mNotifList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        View nView;

        public CircleImageView mNotifImage;
        public TextView mNotifName;
        public TextView mNotifMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            nView= itemView;

            mNotifImage= nView.findViewById(R.id.notif_image);
            mNotifMessage=nView.findViewById(R.id.notif_message);
            mNotifName=nView.findViewById(R.id.notif_name);


        }
    }
}
