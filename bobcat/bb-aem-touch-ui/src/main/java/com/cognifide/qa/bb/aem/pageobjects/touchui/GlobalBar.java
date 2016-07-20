/*-
 * #%L
 * Bobcat Parent
 * %%
 * Copyright (C) 2016 Cognifide Ltd.
 * %%
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
 * #L%
 */
package com.cognifide.qa.bb.aem.pageobjects.touchui;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.cognifide.qa.bb.qualifier.PageObject;
import com.cognifide.qa.bb.aem.util.Conditions;
import com.google.inject.Inject;

@PageObject
public class GlobalBar {

  public static final String IS_SELECTED = "is-selected";

  @Inject
  private Conditions conditions;

  @Inject
  private WebDriver webDriver;

  @FindBy(css = ".js-editor-SidePanel-toggle")
  private WebElement toggleSidePanelButton;

  @FindBy(css = "button[data-layer='Edit'][autocomplete='off']")
  private WebElement editModeButton;

  @FindBy(css = "button[data-layer='Preview']")
  private WebElement previewModeButton;

  public static enum AuthoringMode {

    EDIT,
    PREVIEW;
  }

  public void toggleSidePanel() {
    toggleSidePanelButton.click();
  }

  public void switchToEditMode() {
    if (!isInEditMode()) {
      editModeButton.click();
      webDriver.navigate().refresh();
    }
  }

  public void switchToPreviewMode() {
    if (!isInPreviewMode()) {
      previewModeButton.click();
      webDriver.navigate().refresh();
    }
  }

  public AuthoringMode getCurrentMode() {
    return isInPreviewMode() ? AuthoringMode.PREVIEW : AuthoringMode.EDIT;
  }

  private boolean isInPreviewMode() {
    return conditions.classContains(previewModeButton, IS_SELECTED);
  }

  private boolean isInEditMode() {
    return conditions.classContains(editModeButton, IS_SELECTED);
  }
}
