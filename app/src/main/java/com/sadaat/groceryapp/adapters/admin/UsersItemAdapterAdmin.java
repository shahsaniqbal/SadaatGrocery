package com.sadaat.groceryapp.adapters.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.UserModel;
import com.sadaat.groceryapp.temp.UserTypes;

import java.util.ArrayList;

public class UsersItemAdapterAdmin extends RecyclerView.Adapter<UsersItemAdapterAdmin.ViewHolder> {

    private final ArrayList<UserModel> localDataSet;
    private final int LAYOUT_FILE = R.layout.admin_item_usermgmt_users;
    public UserItemClickListeners customOnClickListener;
    private Boolean putListenerEventOnFullUserView;

    private UsersItemAdapterAdmin(ArrayList<UserModel> localDataSet) {
        this.localDataSet = localDataSet;
    }

    public UsersItemAdapterAdmin(ArrayList<UserModel> localDataSet, Boolean putListenerEventOnFullUserView, UserItemClickListeners customOnClickListener) {
        this(localDataSet);

        this.putListenerEventOnFullUserView = putListenerEventOnFullUserView;

        if (putListenerEventOnFullUserView) {
            this.customOnClickListener = (UserItemClickListenersFull) customOnClickListener;
        } else {
            this.customOnClickListener = (UserItemClickListenersOnlyChild) customOnClickListener;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(LAYOUT_FILE, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.getTxvName().setText(localDataSet.get(position).getFullName());
        viewHolder.getTxvEmail().setText(localDataSet.get(position).getEmailAddress());
        viewHolder.getTxvMobile().setText(formatMobilePhoneNumber(localDataSet.get(position).getMobileNumber()));

        //TODO Set User DP

        // TODO Change this viewHolder.getImgvDisplayPicture().setImageResource(R.drawable.ic_users);

        viewHolder.getImgvUserTypeAvatar().setImageResource(UserTypes.getRelevantUserAvatar(localDataSet.get(position).getUserType()));

        viewHolder.getBtnProfileAndCredits().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onUserProfileAndCreditsButtonClick(v, viewHolder.getAdapterPosition());
            }
        });

        viewHolder.getBtnShowFullDetailsDialogue().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onShowFullDetailsButtonClick(v, viewHolder.getAdapterPosition());
            }
        });

        viewHolder.getImgbCallToPhoneNumber().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customOnClickListener.onCallToPhoneNumberViaSimButtonClick(
                        v,
                        viewHolder.getAdapterPosition(),
                        deformityMobilePhoneNumber(localDataSet.get(viewHolder.getAdapterPosition()).getMobileNumber()));
            }
        });

        if (putListenerEventOnFullUserView){
            viewHolder.getMainView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((UserItemClickListenersFull) customOnClickListener).onFullUserClicked(v, viewHolder.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public void addUser(UserModel model) {
        localDataSet.add(model);
        notifyItemInserted(localDataSet.size() - 1);
    }

    public void deleteUser(int index) {
        localDataSet.remove(index);
        notifyItemRemoved(index);
    }

    public void deleteAll() {
        int size = localDataSet.size();
        localDataSet.clear();
        notifyItemRangeRemoved(0, size);
    }

    /*public void replaceAllData(ArrayList<UserModel> allUsers) {
        deleteAll();
        // TODO Modify this method
        for(UserModel m: allUsers){
            addUser(m);
        }
    }*/


    private String formatMobilePhoneNumber(String rawMobileNumber) {
        //TODO
        return rawMobileNumber;
    }

    private String deformityMobilePhoneNumber(String formattedMobileNumber) {
        //TODO
        return formattedMobileNumber;
    }


    private interface UserItemClickListeners {

        void onShowFullDetailsButtonClick(View v, int position);

        void onUserProfileAndCreditsButtonClick(View v, int position);

        void onCallToPhoneNumberViaSimButtonClick(View v, int position, String mobileNumber);
    }

    public interface UserItemClickListenersOnlyChild extends UserItemClickListeners {
    }

    public interface UserItemClickListenersFull extends UserItemClickListeners {
        /**
         * Try to Avoid Full Item Click Listener
         * This Comment is Added by AHSAN IQBAL while coding this functionality 10-02-2022 - 07:76 pm
         */
        void onFullUserClicked(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final MaterialTextView txvName;
        private final MaterialTextView txvEmail;
        private final MaterialTextView txvMobile;
        private final MaterialCardView btnShowFullDetailsDialogue;
        private final MaterialCardView btnProfileAndCredits;
        private final ImageView imgvDisplayPicture;
        private final ImageView imgvUserTypeAvatar;
        private final ImageButton imgbCallToPhoneNumber;
        private final View mainView;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = (MaterialTextView) view.findViewById(R.id.txv_name_admin_show_users);
            txvEmail = (MaterialTextView) view.findViewById(R.id.txv_email_admin_show_users);
            txvMobile = (MaterialTextView) view.findViewById(R.id.txv_mobile_admin_show_users);

            btnShowFullDetailsDialogue = (MaterialCardView) view.findViewById(R.id.btn_showFullDetails_admin_show_users);
            btnProfileAndCredits = (MaterialCardView) view.findViewById(R.id.btn_userProfileCredits_admin_show_users);

            imgvDisplayPicture = (ImageView) view.findViewById(R.id.imgv_dp_admin_show_users);
            imgvUserTypeAvatar = (ImageView) view.findViewById(R.id.imgv_user_type_admin_show_users);

            imgbCallToPhoneNumber = (ImageButton) view.findViewById(R.id.imgbtn_Call_admin_show_users);
        }

        public MaterialTextView getTxvName() {
            return txvName;
        }

        public MaterialTextView getTxvEmail() {
            return txvEmail;
        }

        public MaterialTextView getTxvMobile() {
            return txvMobile;
        }

        public MaterialCardView getBtnShowFullDetailsDialogue() {
            return btnShowFullDetailsDialogue;
        }

        public MaterialCardView getBtnProfileAndCredits() {
            return btnProfileAndCredits;
        }

        public ImageView getImgvDisplayPicture() {
            return imgvDisplayPicture;
        }

        public ImageView getImgvUserTypeAvatar() {
            return imgvUserTypeAvatar;
        }

        public ImageButton getImgbCallToPhoneNumber() {
            return imgbCallToPhoneNumber;
        }

        public View getMainView() {
            return mainView;
        }
    }

}
