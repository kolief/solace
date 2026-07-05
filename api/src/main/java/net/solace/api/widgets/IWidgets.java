package net.solace.api.widgets;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.InterfaceAddress;

public interface IWidgets {
    public List<IWidget> getAll(int var1);

    public IWidget get(int var1, int var2);

    public IWidget get(int var1, int var2, int var3);

    public IWidget get(int var1);

    @Deprecated(forRemoval=true)
    default public IWidget get(InterfaceAddress interfaceAddress) {
        return this.get(interfaceAddress.getGroup(), interfaceAddress.getChild());
    }

    public IWidget get(int var1, Predicate<? super IWidget> var2);

    public IWidget getChild(int var1, int var2);

    public boolean isVisible(IWidget var1);

    public boolean isVisible(int var1, int var2);

    public boolean isVisible(int var1, int var2, int var3);

    public boolean isVisible(int var1);

    @Deprecated(forRemoval=true)
    default public boolean isVisible(InterfaceAddress interfaceAddress) {
        return this.isVisible(interfaceAddress.getGroup(), interfaceAddress.getChild());
    }

    public List<IWidget> getChildren(IWidget var1, Predicate<IWidget> var2);

    public List<IWidget> getChildren(int var1, int var2, Predicate<IWidget> var3);

    public List<IWidget> getChildren(int var1, int var2, int var3, Predicate<IWidget> var4);

    public List<IWidget> getChildren(int var1, Predicate<IWidget> var2);

    @Deprecated(forRemoval=true)
    default public List<IWidget> getChildren(InterfaceAddress interfaceAddress, Predicate<IWidget> filter) {
        return this.getChildren(interfaceAddress.getGroup(), interfaceAddress.getChild(), filter);
    }

    public void closeInterfaces();
}

