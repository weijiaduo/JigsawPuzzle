package com.promote.imagesplitter;

import android.graphics.Bitmap;
import android.util.Log;

import com.promote.imagesplitter.ImagePiece;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ImageSplitter {
    /**
     * 将图片切成 , piece *piece
     *
     * @param bitmap
     * @param piece
     * @return
     */
    public static List<ImagePiece> split(Bitmap bitmap, int piece)
    {

        List<ImagePiece> pieces = new ArrayList<ImagePiece>(piece * piece);

        int width = bitmap.getWidth() / piece;
        int height = bitmap.getHeight() / piece;

        int pieceWidth = Math.min(width, height) / piece;

        for (int i = 0; i < piece; i++)
        {
            for (int j = 0; j < piece; j++)
            {
                ImagePiece imagePiece = new ImagePiece();
                imagePiece.index = j + i * piece;

                Log.e("TAG", "imagePiece.index" + (j + i * piece));

                int xValue = j * width;
                int yValue = i * height;

                imagePiece.bitmap = Bitmap.createBitmap(bitmap, xValue, yValue,
                        width, height);
                pieces.add(imagePiece);
            }
        }
        return pieces;
    }
}
