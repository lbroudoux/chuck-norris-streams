/*
 * Copyright 2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package nexgen;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

import com.google.common.io.Resources;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

/**
 * A sample transform
 */
@Component(value = "myTransformer")
public class MyTransformer {


    public String content() throws IOException {
        InputStream inputStream = getClass()
            .getClassLoader().getResourceAsStream("data/facts.json");
            
            StringWriter writer = new StringWriter();
            
            IOUtils.copy(inputStream, writer,"UTF-8");

        return writer.toString();
    }

    public String transform() {
        // let's return a random string
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 3; i++) {
            int number = (int) (Math.round(Math.random() * 1000) % 10);
            char letter = (char) ('0' + number);
            buffer.append(letter);
        }
        return buffer.toString();
    }

}
