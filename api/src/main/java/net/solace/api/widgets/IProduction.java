package net.solace.api.widgets;

import java.util.function.Predicate;
import net.solace.api.commons.Predicates;
import net.solace.api.widgets.ProductionQuantity;

public interface IProduction {
    public boolean isOpen();

    public void chooseOption(Predicate<String> var1);

    default public void chooseOption(String option) {
        this.chooseOption(Predicates.textContains(option, false));
    }

    public void selectOtherQuantity();

    public void chooseOption(int var1);

    public void choosePreviousOption();

    public boolean isEnterInputOpen();

    public void enterAmount(int var1);

    public void enterName(String var1);

    public void selectItem(String var1);

    public void selectItem(int var1);

    public int getMakeXQuantity();

    public boolean selectMakeXQuantity(int var1);

    public ProductionQuantity getSelectedQuantity();

    public boolean selectQuantity(ProductionQuantity var1);
}

