/*
 * Copyright (C) 2016 cketti
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


import android.content.ContentResolver;
import android.content.Context;
import android.os.Build;


public final class SafeContentResolverCompat {
    public static SafeContentResolver newInstance(Context context) {
        ContentResolver contentResolver = context.getContentResolver();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return new SafeContentResolverApi14(contentResolver);
        } else {
            return new SafeContentResolverApi21(contentResolver);
        }
    }
}
