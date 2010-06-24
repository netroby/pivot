/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pivot.tutorials.webqueries;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Dictionary;
import org.apache.pivot.util.CalendarDate;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Sheet;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.TextInput;

/**
 * Sheet that allows a user to add or edit an expense record.
 */
public class ExpenseSheet extends Sheet implements Bindable {
    @BXML private Spinner dateSpinner = null;
    @BXML private ListButton typeListButton = null;
    @BXML private TextInput amountTextInput = null;

    @BXML private PushButton cancelButton = null;
    @BXML private PushButton okButton = null;

    private Resources resources = null;

    @Override
    public void initialize(Dictionary<String, Object> context, Resources resource) {
        this.resources = resource;

        cancelButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                close(false);
            }
        });

        okButton.getButtonPressListeners().add(new ButtonPressListener() {
            @Override
            public void buttonPressed(Button button) {
                close(true);
            }
        });
    }

    @Override
    public void close(boolean result) {
        int errorCount = 0;

        if (result) {
            // Validate the form contents
            if (typeListButton.getSelectedIndex() == -1) {
                Form.setFlag(typeListButton, new Form.Flag(MessageType.ERROR,
                    resources.getString("typeRequired")));
                errorCount++;
            } else {
                Form.setFlag(typeListButton, (Form.Flag)null);
            }

            if (amountTextInput.getTextLength() == 0) {
                Form.setFlag(amountTextInput, new Form.Flag(MessageType.ERROR,
                    resources.getString("amountRequired")));
                errorCount++;
            } else {
                Form.setFlag(amountTextInput, (Form.Flag)null);
            }
        }

        if (errorCount == 0) {
            super.close(result);
        }
    }

    @Override
    public void clear() {
        super.clear();

        // Set date to today
        dateSpinner.setSelectedItem(new CalendarDate());
    }
}