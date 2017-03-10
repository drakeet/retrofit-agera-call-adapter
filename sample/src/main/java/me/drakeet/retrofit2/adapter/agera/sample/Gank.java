/*
 * Copyright (C) 2016 drakeet.
 *     http://drakeet.me
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

package me.drakeet.retrofit2.adapter.agera.sample;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Created by drakeet on 16/5/30.
 */
public class Gank {

    @SerializedName("error") public boolean error;

    @SerializedName("results") public List<ResultsEntity> results;


    public static class ResultsEntity {

        @SerializedName("_id") public String id;
        @SerializedName("createdAt") public String createdAt;
        @SerializedName("desc") public String desc;
        @SerializedName("publishedAt") public String publishedAt;
        @SerializedName("source") public String source;
        @SerializedName("type") public String type;
        @SerializedName("url") public String url;
        @SerializedName("used") public boolean used;
        @SerializedName("who") public String who;


        @Override public String toString() {
            return "ResultsEntity{" +
                "id='" + id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", desc='" + desc + '\'' +
                ", publishedAt='" + publishedAt + '\'' +
                ", source='" + source + '\'' +
                ", type='" + type + '\'' +
                ", url='" + url + '\'' +
                ", used=" + used +
                ", who='" + who + '\'' +
                '}';
        }
    }


    @Override public String toString() {
        return "Gank{" +
            "error=" + error +
            ", results=" + results +
            '}';
    }
}
