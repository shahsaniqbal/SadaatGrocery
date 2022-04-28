package com.sadaat.groceryapp.adapters.admin;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.sadaat.groceryapp.R;
import com.sadaat.groceryapp.models.Users.UserModel;
import com.sadaat.groceryapp.temp.FirebaseDataKeys;
import com.sadaat.groceryapp.temp.UserLive;
import com.sadaat.groceryapp.temp.UserTypes;
import com.sadaat.groceryapp.ui.Loaders.LoadingDialogue;

import java.text.MessageFormat;
import java.util.ArrayList;

public class UsersItemAdapterAdmin extends RecyclerView.Adapter<UsersItemAdapterAdmin.ViewHolder> {

    private final ArrayList<UserModel> localDataSet;
    public UserItemClickListeners customOnClickListener;
    private Boolean putListenerEventOnFullUserView;
    private LoadingDialogue dialogue;

    private UsersItemAdapterAdmin(ArrayList<UserModel> localDataSet) {
        this.localDataSet = localDataSet;
    }

    public UsersItemAdapterAdmin(FragmentActivity activity, ArrayList<UserModel> localDataSet, Boolean putListenerEventOnFullUserView, UserItemClickListeners customOnClickListener) {
        this(localDataSet);

        this.putListenerEventOnFullUserView = putListenerEventOnFullUserView;

        if (putListenerEventOnFullUserView) {
            this.customOnClickListener = (UserItemClickListenersFull) customOnClickListener;
        } else {
            this.customOnClickListener = (UserItemClickListenersOnlyChild) customOnClickListener;
        }

        dialogue = new LoadingDialogue(activity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        int LAYOUT_FILE = R.layout.admin_item_usermgmt_users;
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

        Log.e(localDataSet.get(viewHolder.getAdapterPosition()).getEmailAddress(), localDataSet.get(viewHolder.getAdapterPosition()).getDetails().getImageReference());

        if (localDataSet.get(viewHolder.getAdapterPosition()).getDetails() != null) {
            if (!localDataSet.get(viewHolder.getAdapterPosition()).getDetails().getImageReference().isEmpty()) {

                dialogue.show("Showing", "User Display Image");

                FirebaseStorage
                        .getInstance(FirebaseDataKeys.STORAGE_BUCKET_ADDRESS)
                        .getReference()
                        .child(localDataSet.get(viewHolder.getAdapterPosition()).getDetails().getImageReference())
                        .getBytes(10*1024*1024).addOnSuccessListener(bytes -> {
                    viewHolder.getImgvDisplayPicture().setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                }).addOnFailureListener(Throwable::printStackTrace);

                dialogue.dismiss();

            }
        }

        if (!localDataSet.get(viewHolder.getAdapterPosition()).getUID().equalsIgnoreCase(UserLive.currentLoggedInUser.getUID())) {

            viewHolder.getImgbCallToPhoneNumber().setVisibility(View.VISIBLE);

            viewHolder.getTxvPendingCredits().setText(
                    MessageFormat.format("{0}", localDataSet.get(viewHolder.getAdapterPosition()).getCredits().getPendingCredits())
            );

            viewHolder.getTxvWalletCredits().setText(
                    MessageFormat.format("{0}", localDataSet.get(viewHolder.getAdapterPosition()).getCredits().getOwningCredits())
            );

            viewHolder.getMainView().findViewById(R.id.row_for_credits).setVisibility(View.VISIBLE);
            viewHolder.getMainView().findViewById(R.id.row_for_user_detail_buttons).setVisibility(View.VISIBLE);

        }

        else{
            viewHolder.getImgbCallToPhoneNumber().setVisibility(View.GONE);

            viewHolder.getTxvPendingCredits().setText(
                    ""
            );

            viewHolder.getTxvWalletCredits().setText(
                    ""
            );

            viewHolder.getMainView().findViewById(R.id.row_for_credits).setVisibility(View.GONE);
            viewHolder.getMainView().findViewById(R.id.row_for_user_detail_buttons).setVisibility(View.GONE);

        }

        viewHolder.getImgvUserTypeAvatar().setImageResource(UserTypes.getRelevantUserAvatar(localDataSet.get(position).getUserType()));

        viewHolder.getBtnProfileAndCredits().setOnClickListener(v -> customOnClickListener.onUserProfileAndCreditsButtonClick(v, viewHolder.getAdapterPosition()));

        viewHolder.getBtnShowFullDetailsDialogue().setOnClickListener(v -> customOnClickListener.onShowFullDetailsButtonClick(v, viewHolder.getAdapterPosition()));

        viewHolder.getImgbCallToPhoneNumber().setOnClickListener(v -> customOnClickListener.onCallToPhoneNumberViaSimButtonClick(
                v,
                viewHolder.getAdapterPosition(),
                deformityMobilePhoneNumber(localDataSet.get(viewHolder.getAdapterPosition()).getMobileNumber())));

        if (putListenerEventOnFullUserView) {
            viewHolder.getMainView().setOnClickListener(v -> ((UserItemClickListenersFull) customOnClickListener).onFullUserClicked(v, viewHolder.getAdapterPosition()));
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
        private final ImageView imgbCallToPhoneNumber;
        private final MaterialTextView txvPendingCredits;
        private final MaterialTextView txvWalletCredits;

        private final View mainView;


        public ViewHolder(View view) {
            super(view);

            this.mainView = view;

            txvName = (MaterialTextView) view.findViewById(R.id.txv_name_admin_show_users);
            txvEmail = (MaterialTextView) view.findViewById(R.id.txv_email_admin_show_users);
            txvMobile = (MaterialTextView) view.findViewById(R.id.txv_mobile_admin_show_users);
            txvPendingCredits = (MaterialTextView) view.findViewById(R.id.txv_pending_admin_show_users);
            txvWalletCredits = (MaterialTextView) view.findViewById(R.id.txv_wallet_admin_show_users);

            btnShowFullDetailsDialogue = (MaterialCardView) view.findViewById(R.id.btn_showFullDetails_admin_show_users);
            btnProfileAndCredits = (MaterialCardView) view.findViewById(R.id.btn_userProfileCredits_admin_show_users);

            imgvDisplayPicture = (ImageView) view.findViewById(R.id.imgv_dp_admin_show_users);
            imgvUserTypeAvatar = (ImageView) view.findViewById(R.id.imgv_user_type_admin_show_users);

            imgbCallToPhoneNumber = (ImageView) view.findViewById(R.id.imgbtn_Call_admin_show_users);
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

        public ImageView getImgbCallToPhoneNumber() {
            return imgbCallToPhoneNumber;
        }

        public View getMainView() {
            return mainView;
        }

        public MaterialTextView getTxvPendingCredits() {
            return txvPendingCredits;
        }

        public MaterialTextView getTxvWalletCredits() {
            return txvWalletCredits;
        }
    }

}
