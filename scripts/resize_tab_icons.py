from PIL import Image
import os
import shutil

ASSETS = r"C:\Users\Admin\.cursor\projects\c-Users-Admin-Documents-Projects-Storm\assets"
PLUGINS = r"C:\Users\Admin\Documents\Projects\Storm_noauth\S2 Loader Jan 2026\ui\src\main\resources\net\solace\ui\plugins"
BACKUP = os.path.join(PLUGINS, "_backup_original")

SOURCES = {
    "settings.png": os.path.join(
        ASSETS,
        "c__Users_Admin_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_Gemini_Generated_Image_aciqvlaciqvlaciq-11c480ed-2c42-42cc-8481-ce1e4f0afde0.png",
    ),
    "download.png": os.path.join(
        ASSETS,
        "c__Users_Admin_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_Gemini_Generated_Image_8af99n8af99n8af9-d7186636-10fd-4838-bf07-34a2ffe7ceb7.png",
    ),
    "profiles.png": os.path.join(
        ASSETS,
        "c__Users_Admin_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_Gemini_Generated_Image_10kmx410kmx410km-7f2e336a-b945-43bc-a097-36474b29846b.png",
    ),
    "plugin.png": os.path.join(
        ASSETS,
        "c__Users_Admin_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_Gemini_Generated_Image_fu8ft8fu8ft8fu8f-e8f23ab1-bfdb-4de1-84ae-4e328b4f886c.png",
    ),
}

TARGETS = {
    "settings.png": (32, 32),
    "download.png": (32, 35),
    "profiles.png": (32, 32),
    "plugin.png": (32, 35),
}


def white_to_alpha(img, threshold=240):
    img = img.convert("RGBA")
    pixels = img.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            r, g, b, a = pixels[x, y]
            if r >= threshold and g >= threshold and b >= threshold:
                pixels[x, y] = (r, g, b, 0)
    return img


def trim_transparent(img):
    bbox = img.getbbox()
    return img.crop(bbox) if bbox else img


def fit_resize(img, size):
    img = trim_transparent(img)
    w, h = img.size
    tw, th = size
    scale = min(tw / w, th / h)
    nw, nh = max(1, int(w * scale)), max(1, int(h * scale))
    resized = img.resize((nw, nh), Image.Resampling.NEAREST)
    canvas = Image.new("RGBA", size, (0, 0, 0, 0))
    ox = (tw - nw) // 2
    oy = (th - nh) // 2
    canvas.paste(resized, (ox, oy), resized)
    return canvas


def main():
    os.makedirs(BACKUP, exist_ok=True)
    for name, size in TARGETS.items():
        dest = os.path.join(PLUGINS, name)
        if os.path.exists(dest):
            shutil.copy2(dest, os.path.join(BACKUP, name))
        img = white_to_alpha(Image.open(SOURCES[name]))
        fit_resize(img, size).save(dest, "PNG")
        print(f"Wrote {name} at {size}")


if __name__ == "__main__":
    main()
    print(f"Backups saved to {BACKUP}")
