package correa.resembrink.dev.notificationsbd;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{


    private List<Users> usersList;

    private Context context;



    public UserRecyclerAdapter(Context context, List<Users> usersList) {
        this.usersList = usersList;
        this.context=context;

    }

    @NonNull
    @Override
    public UserRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item,parent, false);
        return  new ViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull UserRecyclerAdapter.ViewHolder holder, int position) {

        final String user_name= usersList.get(position).getName();

        holder.user_name_view.setText(usersList.get(position).getName());

        CircleImageView user_image_view=holder.user_imageview;
        Glide.with(context).load(usersList.get(position).getImage()).into(user_image_view);

        final String user_id=usersList.get(position).userId;

        holder.nView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent sendIntent= new Intent(context,SendActivity.class);
                sendIntent.putExtra("user_id", user_id);
                sendIntent.putExtra("user_name", user_name);
                context.startActivity(sendIntent);



            }
        });


    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private View nView;

        private CircleImageView user_imageview;
        private TextView user_name_view;

        public ViewHolder(View itemView) {
            super(itemView);

            nView= itemView;

            user_imageview=nView.findViewById(R.id.user_list_image);
            user_name_view=nView.findViewById(R.id.user_list_name);

        }
    }

}
