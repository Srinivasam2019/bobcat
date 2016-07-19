package com.cognifide.qa.bb.aem.pageobjects.touchui;

import static com.cognifide.qa.bb.aem.util.DataPathUtil.JCR_CONTENT;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.stalenessOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.cognifide.qa.bb.constants.Timeouts;
import com.cognifide.qa.bb.qualifier.CurrentScope;
import com.cognifide.qa.bb.qualifier.Global;
import com.cognifide.qa.bb.qualifier.PageObject;
import com.cognifide.qa.bb.aem.data.componentconfigs.FieldConfig;
import com.cognifide.qa.bb.aem.util.Conditions;
import com.cognifide.qa.bb.aem.util.DataPathUtil;
import com.google.inject.Inject;

@PageObject
public class Parsys {

  public static final String CSS = ".cq-Overlay.cq-Overlay--component.cq-Overlay--container";

  private static final String IS_SELECTED = "is-selected";

  @Inject
  private Conditions conditions;

  @FindBy(css = ".cq-Overlay--placeholder[data-text='Drag components here']")
  private WebElement dropArea;

  @FindBy(css = Component.CSS)
  private List<Component> componentList;

  @Global
  @FindBy(css = InsertComponentWindow.CSS)
  private InsertComponentWindow insertComponentWindow;

  @Inject
  @CurrentScope
  private WebElement parsys;

  public String getDataPath() {
    String rawValue = parsys.getAttribute("data-path");
    return StringUtils.substringAfter(rawValue, JCR_CONTENT);
  }

  public InsertComponentWindow openInsertDialog() {
    tryToSelect();
    tryToOpenInsertWindow();
    return insertComponentWindow;
  }

  public Component getComponent(String dataPath) {
    String componentDataPath = DataPathUtil.normalize(dataPath);
    return componentList.stream() //
        .filter(containsDataPath(componentDataPath)) //
        .findFirst() //
        .orElseThrow(() -> new IllegalStateException("Component not present in the parsys"));
  }

  public boolean isComponentPresent(String dataPath) {
    String componentDataPath = DataPathUtil.normalize(dataPath);
    return componentList.stream() //
        .anyMatch(containsDataPath(componentDataPath));
  }

  public void insertComponent(String title) {
    openInsertDialog().insertComponent(title);
  }

  public void configureComponent(String dataPath, Map<String, List<FieldConfig>> data) {
    getComponent(dataPath).configure(data);
  }

  public void deleteComponent(String dataPath) {
    getComponent(dataPath).delete();
  }

  public boolean isNotStale() {
    return conditions.isConditionMet(not(stalenessOf(parsys)));
  }

  private void tryToSelect() {
    conditions.verify(input -> {
      conditions.verify(visibilityOf(dropArea)).click();
      return dropArea.getAttribute("class").contains(IS_SELECTED);
    }, Timeouts.MEDIUM);
  }

  /**
   * it may happen that the window pops up just a moment before {@code dropArea.click(} happens, which
   * results in WebdriverException: 'Other element would receive the click' - thus it is catched and
   * validated
   */
  private void tryToOpenInsertWindow() {
    conditions.verify(ignored -> {
      try {
        dropArea.click();
      } catch (WebDriverException e) {
        return e.getMessage().contains("Other element would receive the click");
      }
      return insertComponentWindow.isDisplayedExpectingComponents();
    }, Timeouts.MEDIUM);
  }

  private Predicate<Component> containsDataPath(String componentDataPath) {
    return component -> StringUtils.contains(component.getDataPath(), componentDataPath);
  }
}
