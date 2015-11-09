/*
 * This is the source code of Telegram for Android v. 3.x.x.
 * It is licensed under GNU GPL v. 2 or later.
 * You should have received a copy of the license in this archive (see LICENSE).
 *
 * Copyright Nikolai Kudashov, 2013-2015.
 */

package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;

import org.telegram.SQLite.SQLiteCursor;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.LaunchActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;

@TargetApi(Build.VERSION_CODES.M)
public class TgChooserTargetService extends ChooserTargetService {

    private Paint roundPaint;
    private RectF bitmapRect;

    @Override
    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
        final List<ChooserTarget> targets = new ArrayList<>();
        if (!UserConfig.isClientActivated()) {
            return targets;
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        final Semaphore semaphore = new Semaphore(0);
        final ComponentName componentName = new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName());
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable() {
            @Override
            public void run() {
                ArrayList<Integer> dialogs = new ArrayList<>();
                ArrayList<TLRPC.Chat> chats = new ArrayList<>();
                ArrayList<TLRPC.User> users = new ArrayList<>();
                try {
                    ArrayList<Integer> usersToLoad = new ArrayList<>();
                    usersToLoad.add(UserConfig.getClientUserId());
                    ArrayList<Integer> chatsToLoad = new ArrayList<>();
                    SQLiteCursor cursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", 0, 20));
                    while (cursor.next()) {
                        long id = cursor.longValue(0);

                        int lower_id = (int) id;
                        int high_id = (int) (id >> 32);
                        if (lower_id != 0) {
                            if (high_id == 1) {
                                continue;
                            } else {
                                if (lower_id > 0) {
                                    if (!usersToLoad.contains(lower_id)) {
                                        usersToLoad.add(lower_id);
                                    }
                                } else {
                                    if (!chatsToLoad.contains(-lower_id)) {
                                        chatsToLoad.add(-lower_id);
                                    }
                                }
                            }
                        } else {
                            continue;
                        }
                        dialogs.add(lower_id);
                        if (dialogs.size() == 8) {
                            break;
                        }
                    }
                    cursor.dispose();
                    if (!chatsToLoad.isEmpty()) {
                        MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                    }
                    if (!usersToLoad.isEmpty()) {
                        MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", usersToLoad), users);
                    }
                } catch (Exception e) {
                    FileLog.e("tmessages", e);
                }
                for (int a = 0; a < dialogs.size(); a++) {
                    float score = (a + 1) / 20.0f;
                    Bundle extras = new Bundle();
                    Icon icon = null;
                    String name = null;
                    int id = dialogs.get(a);
                    if (id > 0) {
                        for (int b = 0; b < users.size(); b++) {
                            TLRPC.User user = users.get(b);
                            if (user.id == id) {
                                extras.putLong("dialogId", (long) id);
                                if (user.photo != null && user.photo.photo_small != null) {
                                    icon = createRoundBitmap(FileLoader.getPathToAttach(user.photo.photo_small, true));
                                }
                                name = ContactsController.formatName(user.first_name, user.last_name);
                                break;
                            }
                        }
                    } else {
                        for (int b = 0; b < chats.size(); b++) {
                            TLRPC.Chat chat = chats.get(b);
                            if (chat.id == -id) {
                                extras.putLong("dialogId", (long) id);
                                if (chat.photo != null && chat.photo.photo_small != null) {
                                    icon = createRoundBitmap(FileLoader.getPathToAttach(chat.photo.photo_small, true));
                                }
                                name = chat.title;
                                break;
                            }
                        }
                    }
                    if (name != null) {
                        if (icon == null) {
                            icon = Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.logo_avatar);
                        }
                        targets.add(new ChooserTarget(name, icon, score, componentName, extras));
                    }
                }
                semaphore.release();
            }
        });
        try {
            semaphore.acquire();
        } catch (Exception e) {
            FileLog.e("tmessages", e);
        }
        return targets;
    }

    private Icon createRoundBitmap(File path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path.toString());
            if (bitmap != null) {
                Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                result.eraseColor(Color.TRANSPARENT);
                Canvas canvas = new Canvas(result);
                BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                if (roundPaint == null) {
                    roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    bitmapRect = new RectF();
                }
                roundPaint.setShader(shader);
                bitmapRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                canvas.drawRoundRect(bitmapRect, bitmap.getWidth(), bitmap.getHeight(), roundPaint);
                return Icon.createWithBitmap(result);
            }
        } catch (Throwable e) {
            FileLog.e("tmessages", e);
        }
        return null;
    }

}
