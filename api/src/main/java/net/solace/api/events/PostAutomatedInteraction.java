package net.solace.api.events;

import net.solace.api.interact.AutomatedMenu;

public final class PostAutomatedInteraction {
    private final AutomatedMenu menu;

    public PostAutomatedInteraction(AutomatedMenu menu) {
        this.menu = menu;
    }

    public AutomatedMenu getMenu() {
        return this.menu;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PostAutomatedInteraction)) {
            return false;
        }
        PostAutomatedInteraction other = (PostAutomatedInteraction)o;
        AutomatedMenu this$menu = this.getMenu();
        AutomatedMenu other$menu = other.getMenu();
        return !(this$menu == null ? other$menu != null : !this$menu.equals(other$menu));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        AutomatedMenu $menu = this.getMenu();
        result = result * 59 + ($menu == null ? 43 : $menu.hashCode());
        return result;
    }

    public String toString() {
        return "PostAutomatedInteraction(menu=" + String.valueOf(this.getMenu()) + ")";
    }
}

