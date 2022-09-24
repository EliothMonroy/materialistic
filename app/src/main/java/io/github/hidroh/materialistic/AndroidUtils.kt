/*
 * Copyright (c) 2017 Ha Duy Trung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.hidroh.materialistic

/**
 * Straight copies of Android's static utilities
 */
interface AndroidUtils {
    object TextUtils {
        /**
         * Returns true if a and b are equal, including if they are both null.
         *
         * *Note: In platform versions 1.1 and earlier, this method only worked well if
         * both the arguments were instances of String.*
         *
         * @param a first CharSequence to check
         * @param b second CharSequence to check
         * @return true if a and b are equal
         */
        @JvmStatic
        fun equals(a: CharSequence?, b: CharSequence?): Boolean {
            if (a === b) return true
            var length = 0
            return if (a != null && b != null && a.length.also { length = it } == b.length) {
                if (a is String && b is String) {
                    a == b
                } else {
                    for (i in 0 until length) {
                        if (a[i] != b[i]) return false
                    }
                    true
                }
            } else false
        }

        /**
         * Returns true if the string is null or 0-length.
         *
         * @param str the string to be examined
         * @return true if str is null or zero length
         */
        @JvmStatic
        fun isEmpty(str: CharSequence?): Boolean {
            return str == null || str.isEmpty()
        }
    }
}