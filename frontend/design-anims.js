const fs = require('fs');
const path = require('path');
const base = __dirname;
const apps = ['app-android-shop', 'app-android-user', 'app-android-delivery', 'app-android-kiosk'];

const anims = {
    'fade_in.xml': `<?xml version="1.0" encoding="utf-8"?>
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromAlpha="0.0"
    android:toAlpha="1.0"
    android:duration="250"
    android:interpolator="@android:anim/decelerate_interpolator" />`,

    'fade_out.xml': `<?xml version="1.0" encoding="utf-8"?>
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromAlpha="1.0"
    android:toAlpha="0.0"
    android:duration="200"
    android:interpolator="@android:anim/accelerate_interpolator" />`,

    'slide_up.xml': `<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/decelerate_interpolator">
    <translate
        android:fromYDelta="8%p"
        android:toYDelta="0"
        android:duration="300" />
    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0"
        android:duration="300" />
</set>`,

    'slide_down.xml': `<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/accelerate_interpolator">
    <translate
        android:fromYDelta="0"
        android:toYDelta="8%p"
        android:duration="250" />
    <alpha
        android:fromAlpha="1.0"
        android:toAlpha="0.0"
        android:duration="250" />
</set>`,

    'slide_in_right.xml': `<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/decelerate_interpolator">
    <translate
        android:fromXDelta="5%p"
        android:toXDelta="0"
        android:duration="300" />
    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0"
        android:duration="300" />
</set>`,

    'slide_out_left.xml': `<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/accelerate_interpolator">
    <translate
        android:fromXDelta="0"
        android:toXDelta="-5%p"
        android:duration="250" />
    <alpha
        android:fromAlpha="1.0"
        android:toAlpha="0.0"
        android:duration="250" />
</set>`,

    'scale_up.xml': `<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/overshoot_interpolator">
    <scale
        android:fromXScale="0.95"
        android:fromYScale="0.95"
        android:toXScale="1.0"
        android:toYScale="1.0"
        android:pivotX="50%"
        android:pivotY="50%"
        android:duration="300" />
    <alpha
        android:fromAlpha="0.0"
        android:toAlpha="1.0"
        android:duration="200" />
</set>`,
};

for (const app of apps) {
    const dir = path.join(base, app, 'app', 'src', 'main', 'res', 'anim');
    fs.mkdirSync(dir, { recursive: true });
    for (const [file, content] of Object.entries(anims)) {
        fs.writeFileSync(path.join(dir, file), content, 'utf8');
    }
    console.log(`${app}: ${Object.keys(anims).length} animations written`);
}
console.log('=== ALL ANIMATIONS DONE ===');
