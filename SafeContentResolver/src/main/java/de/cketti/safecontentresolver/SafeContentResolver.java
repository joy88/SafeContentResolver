/*
 * Copyright (C) 2016 cketti
 * Copyright (C) 2016 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.cketti.safecontentresolver;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;


public abstract class SafeContentResolver {
    private final ContentResolver contentResolver;


    public static SafeContentResolver newInstance(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        return new SafeContentResolverApi21(contentResolver);
    }

    protected SafeContentResolver(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public InputStream openInputStream(Uri uri) throws FileNotFoundException {
        String scheme = uri.getScheme();
        if (!ContentResolver.SCHEME_FILE.equals(scheme)) {
            return contentResolver.openInputStream(uri);
        }

        File file = new File(uri.getPath());
        ParcelFileDescriptor parcelFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

        int fileUid = getFileUidOrThrow(fileDescriptor);
        if (fileUid == android.os.Process.myUid()) {
            throw new FileNotFoundException("File is owned by the application itself");
        }


        AssetFileDescriptor fd = new AssetFileDescriptor(parcelFileDescriptor, 0, -1);
        try {
            return fd.createInputStream();
        } catch (IOException e) {
            throw new FileNotFoundException("Unable to create stream");
        }
    }

    protected abstract int getFileUidOrThrow(FileDescriptor fileDescriptor) throws FileNotFoundException;
}
