package com.piapps.flashcard.adapter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.piapps.flashcard.R;
import com.piapps.flashcard.activity.DrawActivity;
import com.piapps.flashcard.activity.FlashcardActivity;
import com.piapps.flashcard.anim.FlipAnimation;
import com.piapps.flashcard.model.Card;
import com.piapps.flashcard.util.Utils;
import com.piapps.flashcard.view.AutoResizeTextView;
import com.piapps.flashcard.view.RecyclerListView;

import java.util.List;

import eltos.simpledialogfragment.input.SimpleInputDialog;

import static com.piapps.flashcard.activity.FlashcardActivity.REQUEST_IMAGE_CAPTURE;
import static com.piapps.flashcard.activity.FlashcardActivity.TAKE_PHOTO_CODE;

/**
 * Created by abduaziz on 2/16/17.
 */

public class RVCardsAdapter extends RecyclerView.Adapter<RVCardsAdapter.ViewHolder> {
    private static final String ENTER_FRONT_DIALOG = "enter_front_dialog";
    List<Card> list;
    RecyclerListView rv;

    public RVCardsAdapter(List<Card> list) {
        this.list = list;
    }

    //should be called after setAdapter(this)
    public void setRV(RecyclerListView rv) {
        this.rv = rv;
    }

    @Override
    public RVCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(RVCardsAdapter.ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeAt(int position) {
        FlashcardActivity.instance.cardDb.deleteCard(list.get(position));
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void addAt(int position, Card card) {
        FlashcardActivity.instance.cardDb.addCard(card);
        list.add(position, card);
        notifyItemInserted(position);
        notifyItemRangeChanged(position, list.size());
        rv.scrollToPosition(position);
    }

    public void flipCard(View front, View back, View root) {
        FlipAnimation flipAnimation = new FlipAnimation(front, back, 312);
        if (front.getVisibility() == View.GONE) {
            flipAnimation.reverse();
        }
        root.startAnimation(flipAnimation);
    }

    public void updateImages(int editingPosition, String fImage, String fPath) {
        if (editingPosition < list.size()) {
            if (!list.get(editingPosition).getFrontImage().equals("no"))
                Utils.deleteExistingImage(list.get(editingPosition).getFrontPath(), list.get(editingPosition).getFrontImage());
            list.get(editingPosition).setFrontPath(fPath);
            list.get(editingPosition).setFrontImage(fImage);
            list.get(editingPosition).setFront("");
            notifyItemChanged(editingPosition);
        } else
            Log.d("ERROR", "updateImages: list size is bigger than editingPosition");
    }

    public void updateImagesBack(int editingPosition, String bImage, String bPath) {
        if (editingPosition < list.size()) {
            if (!list.get(editingPosition).getBackImage().equals("no"))
                Utils.deleteExistingImage(list.get(editingPosition).getBackPath(), list.get(editingPosition).getBackImage());
            list.get(editingPosition).setBackPath(bPath);
            list.get(editingPosition).setBackImage(bImage);
            list.get(editingPosition).setBack("");
            notifyItemChanged(editingPosition);
        } else
            Log.d("ERROR", "updateImages: list size is bigger than editingPosition");
    }

    public void setFrontText(int editingPosition, String text) {
        list.get(editingPosition).setFront(text);
        notifyItemChanged(editingPosition);
    }

    public void setBackText(int editingPosition, String text) {
        list.get(editingPosition).setBack(text);
        notifyItemChanged(editingPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView delete;

        AutoResizeTextView frontText;
        AutoResizeTextView backText;
        ImageView flip;
        ImageView flipBack;
        RelativeLayout front;
        RelativeLayout back;
        ImageView bg;
        ImageView bgBack;
        ImageView editCard;
        ImageView editCardBack;

        public ViewHolder(View itemView) {
            super(itemView);
            frontText = (AutoResizeTextView) itemView.findViewById(R.id.textFront);
            frontText.setOnClickListener(this);
            backText = (AutoResizeTextView) itemView.findViewById(R.id.textBack);
            backText.setOnClickListener(this);
            delete = (ImageView) itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);
            flip = (ImageView) itemView.findViewById(R.id.flip);
            flip.setOnClickListener(this);
            flipBack = (ImageView) itemView.findViewById(R.id.flipBack);
            flipBack.setOnClickListener(this);
            front = (RelativeLayout) itemView.findViewById(R.id.front);
            back = (RelativeLayout) itemView.findViewById(R.id.back);
            back.setGravity(View.GONE);
            bg = (ImageView) itemView.findViewById(R.id.card);
            bgBack = (ImageView) itemView.findViewById(R.id.cardBack);
            editCard = (ImageView) itemView.findViewById(R.id.editCard);
            editCard.setOnClickListener(this);
            editCardBack = (ImageView) itemView.findViewById(R.id.editCardBack);
            editCardBack.setOnClickListener(this);
        }

        void bind(Card card) {
            frontText.setText(card.getFront());
            backText.setText(card.getBack());
            if (card.getFrontImage().equals("no")) {
                bg.setBackgroundColor(Color.parseColor(card.getColor()));
                Bitmap b = BitmapFactory.decodeResource(FlashcardActivity.instance.getResources(), R.color.transparent);
                bg.setImageBitmap(b);
            } else {
                bg.setImageBitmap(Utils.loadImageFromStorage(card.getFrontPath(), card.getFrontImage()));
                frontText.setText("");
            }
            if (card.getBackImage().equals("no")) {
                bgBack.setBackgroundColor(Color.parseColor(card.getColor()));
                Bitmap b = BitmapFactory.decodeResource(FlashcardActivity.instance.getResources(), R.color.transparent);
                bgBack.setImageBitmap(b);
            } else {
                bgBack.setImageBitmap(Utils.loadImageFromStorage(card.getBackPath(), card.getBackImage()));
                backText.setText("");
            }
        }

        @Override
        public void onClick(View view) {
            if (view.equals(delete)) {
                final Card removingCard = list.get(getAdapterPosition());
                final int removingPosition = getAdapterPosition();
                removeAt(getAdapterPosition());
                Snackbar.make(view, "Card deleted", Snackbar.LENGTH_LONG).setAction("CANCEL", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addAt(removingPosition, removingCard);
                    }
                }).show();
            }
            if (view.equals(frontText)) {
                String text = frontText.getText().toString();
                if (text.equals("Example front side of a flashcard, you can add text, image or drawing")
                        || text.equals("Example back side of a flashcard, you can add text, image or drawing"))
                    text = "";
                SimpleInputDialog.build()
                        .msg(R.string.edit_text)
                        .allowEmpty(false)
                        .text(text)
                        .hint(R.string.edit_text_hint)
                        .show(FlashcardActivity.instance, ENTER_FRONT_DIALOG);
                FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                FlashcardActivity.instance.setEditingBack(false);
            }
            if (view.equals(backText)) {
                String text = backText.getText().toString();
                if (text.equals("Example front side of a flashcard, you can add text, image or drawing")
                        || text.equals("Example back side of a flashcard, you can add text, image or drawing"))
                    text = "";
                SimpleInputDialog.build()
                        .msg(R.string.edit_text)
                        .text(text)
                        .allowEmpty(false)
                        .hint(R.string.edit_text_hint_back)
                        .show(FlashcardActivity.instance, ENTER_FRONT_DIALOG);
                FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                FlashcardActivity.instance.setEditingBack(true);
            }
            if (view.equals(flip)) {
                flipCard(front, back, itemView);
            }
            if (view.equals(flipBack)) {
                flipCard(front, back, itemView);
            }
            if (view.equals(editCard)) {


                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), editCard);
                popupMenu.getMenuInflater().inflate(R.menu.edit_card_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.a1) {
                            String text = frontText.getText().toString();
                            if (text.equals("Example front side of a flashcard, you can add text, image or drawing")
                                    || text.equals("Example back side of a flashcard, you can add text, image or drawing"))
                                text = "";
                            SimpleInputDialog.build()
                                    .msg(R.string.enter_set_name)
                                    .allowEmpty(false)
                                    .text(text)
                                    .hint(R.string.hint_enter_set_name)
                                    .show(FlashcardActivity.instance, ENTER_FRONT_DIALOG);
                            FlashcardActivity.instance.setEditingBack(false);
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            return true;
                        }
                        if (item.getItemId() == R.id.a2) {
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(false);
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            int permissionCheck = ContextCompat.checkSelfPermission(FlashcardActivity.instance,
                                    Manifest.permission.CAMERA);
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                if (takePictureIntent.resolveActivity(FlashcardActivity.instance.getPackageManager()) != null) {
                                    FlashcardActivity.instance.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            } else {
                                Toast.makeText(FlashcardActivity.instance, FlashcardActivity.instance.getString(R.string.give_camera_permission), Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                        if (item.getItemId() == R.id.a3) {
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(false);
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            FlashcardActivity.instance.startActivityForResult(Intent.createChooser(intent, "Select Picture"), TAKE_PHOTO_CODE);
                            return true;
                        }
                        if (item.getItemId() == R.id.a4) {
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(false);
                            Log.d("BUG", "onMenuItemClick: position2 = " + getAdapterPosition());
                            Log.d("BUG", "onMenuItemClick: editingPosition = " + FlashcardActivity.instance.getEditingPosition() +
                                    "\nisBack = " + FlashcardActivity.instance.isEditingBack());
                            Intent intent = new Intent(FlashcardActivity.instance, DrawActivity.class);
                            intent.putExtra("isBack", false);
                            FlashcardActivity.instance.startActivityForResult(intent, FlashcardActivity.PICK_DRAWING_REQUEST);
                            return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
            if (view.equals(editCardBack)) {

                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), editCard);
                popupMenu.getMenuInflater().inflate(R.menu.edit_card_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(FlashcardActivity.instance, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();

                        if (item.getItemId() == R.id.a1) {
                            String text = backText.getText().toString();
                            if (text.equals("Example front side of a flashcard, you can add text, image or drawing")
                                    || text.equals("Example back side of a flashcard, you can add text, image or drawing"))
                                text = "";
                            SimpleInputDialog.build()
                                    .msg(R.string.edit_text)
                                    .allowEmpty(false)
                                    .text(text)
                                    .hint(R.string.edit_text_hint_back)
                                    .show(FlashcardActivity.instance, ENTER_FRONT_DIALOG);
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(true);
                            return true;
                        }
                        if (item.getItemId() == R.id.a2) {
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(true);
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            int permissionCheck = ContextCompat.checkSelfPermission(FlashcardActivity.instance,
                                    Manifest.permission.CAMERA);
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                if (takePictureIntent.resolveActivity(FlashcardActivity.instance.getPackageManager()) != null) {
                                    FlashcardActivity.instance.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                }
                            } else {
                                Toast.makeText(FlashcardActivity.instance, FlashcardActivity.instance.getString(R.string.give_camera_permission), Toast.LENGTH_LONG).show();
                            }
                            return true;
                        }
                        if (item.getItemId() == R.id.a3) {
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(true);
                            Intent intent = new Intent();
                            // Show only images, no videos or anything else
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            // Always show the chooser (if there are multiple options available)
                            FlashcardActivity.instance.startActivityForResult(Intent.createChooser(intent, "Select Picture"), TAKE_PHOTO_CODE);
                            return true;
                        }
                        if (item.getItemId() == R.id.a4) {
                            FlashcardActivity.instance.setEditingPosition(getAdapterPosition());
                            FlashcardActivity.instance.setEditingBack(true);
                            Log.d("BUG", "onMenuItemClick: position2 = " + getAdapterPosition());
                            Log.d("BUG", "onMenuItemClick: editingPosition = " + FlashcardActivity.instance.getEditingPosition() +
                                    "\nisBack = " + FlashcardActivity.instance.isEditingBack());
                            Intent intent = new Intent(FlashcardActivity.instance, DrawActivity.class);
                            intent.putExtra("isBack", true);
                            FlashcardActivity.instance.startActivityForResult(intent, FlashcardActivity.PICK_DRAWING_REQUEST);
                            return true;
                        }

                        return false;
                    }
                });
                popupMenu.show();


            }
        }
    }

}
