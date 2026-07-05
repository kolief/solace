from PIL import Image
import os
import shutil

from resize_tab_icons import white_to_alpha, fit_resize

ASSETS = r"C:\Users\Admin\.cursor\projects\c-Users-Admin-Documents-Projects-Storm\assets"
SOURCE = os.path.join(
    ASSETS,
    "c__Users_Admin_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_Gemini_Generated_Image_1gui6h1gui6h1gui-9c050b79-c507-46aa-904c-dbe0a3986dbb.png",
)

LOADER_ICON = r"C:\Users\Admin\Documents\Projects\Storm_noauth\S2 Loader Jan 2026\loader\src\main\resources\net\solace\loader\ui\solace.png"
PLUGINS_ICON = r"C:\Users\Admin\Documents\Projects\Storm_noauth\S2 Loader Jan 2026\ui\src\main\resources\net\solace\ui\plugins\solace.png"
BACKUP_DIR = r"C:\Users\Admin\Documents\Projects\Storm_noauth\S2 Loader Jan 2026\loader\src\main\resources\net\solace\loader\ui\_backup_original"

TARGETS = {
    LOADER_ICON: (136, 155),
    PLUGINS_ICON: (16, 16),
}


def main():
    os.makedirs(BACKUP_DIR, exist_ok=True)
    img = white_to_alpha(Image.open(SOURCE))
    for dest, size in TARGETS.items():
        if os.path.exists(dest):
            shutil.copy2(dest, os.path.join(BACKUP_DIR, os.path.basename(dest)))
        fit_resize(img, size).save(dest, "PNG")
        print(f"Wrote {dest} at {size}")
    print(f"Backups saved to {BACKUP_DIR}")


if __name__ == "__main__":
    main()
