# Design System Document: The Executive Inventory

## 1. Overview & Creative North Star
**Creative North Star: "The Architectural Ledger"**

This design system eschews the cluttered, "dashboard-lite" look of typical SaaS tools in favor of an editorial, high-trust environment. We treat store management as a discipline of precision. The aesthetic is inspired by premium Swiss architectural layouts—prioritizing massive whitespace, intentional asymmetry, and a refusal to use standard borders to define space. 

By utilizing a high-contrast typography scale (Manrope for high-level data and Inter for utility), we create a "read-at-a-glance" hierarchy essential for busy retail environments. We move beyond "clean" into "authoritative," using deep navy tonalities and layered surfaces to create a sense of permanent, reliable infrastructure.

---

## 2. Colors: Depth Over Division
We leverage a sophisticated Material-based palette to build a UI that feels carved from a single block, rather than assembled from parts.

### The "No-Line" Rule
**Standard 1px borders are strictly prohibited for sectioning.** To define a new functional area, use a background shift. For example:
- A data table sits on `surface_container_low` (#f3f3f8).
- Individual row interactions or "hover" states shift to `surface_container_highest` (#e1e2e7).
- This creates a seamless, "app-as-a-canvas" feel that reduces cognitive visual noise.

### Surface Hierarchy & Nesting
Treat the UI as a physical stack of materials.
1.  **Base Layer:** `surface` (#f8f9fd) — The "floor" of the application.
2.  **Sectioning:** `surface_container` (#edeef2) — Used for large structural sidebars or secondary panels.
3.  **Actionable Cards:** `surface_container_lowest` (#ffffff) — Reserved for the most important data cards to make them "pop" against the slightly darker background.

### The "Glass & Signature" Rule
- **CTAs:** Use a subtle linear gradient on primary buttons: `primary` (#003354) to `primary_container` (#004a77). This adds a "weighted" feel that flat hex codes lack.
- **Overlays:** For modals or floating filters, use `surface_container_lowest` with a 80% opacity and a `20px` backdrop-blur. This keeps the store manager oriented by letting the underlying data "bleed" through the edges.

---

## 3. Typography: The Editorial Edge
We use two typefaces to distinguish between **Observation** (Display) and **Operation** (Functional).

*   **Display & Headlines (Manrope):** High-end, geometric, and authoritative. Use `display-lg` (3.5rem) for critical KPIs (e.g., Total Revenue) to create a "Hero Data" moment.
*   **Body & Labels (Inter):** Neutral and highly legible. Use `body-md` (0.875rem) for all inventory lists. 
*   **The Contrast Rule:** Pair a `headline-sm` title with a `label-sm` subtitle in `on_surface_variant` (#41474f). The extreme jump in scale creates a premium "magazine" layout feel that guides the eye naturally.

---

## 4. Elevation & Depth: Tonal Layering
Traditional shadows are a crutch. In this system, we use light to define importance.

*   **The Layering Principle:** Instead of a shadow, place a `surface_container_lowest` element on top of a `surface_container` background. The subtle shift from #edeef2 to #ffffff provides all the separation needed for a high-trust, professional interface.
*   **Ambient Shadows:** If a floating element (like a FAB or Popover) requires a shadow, it must be: `Y: 8px, Blur: 24px, Color: rgba(25, 28, 31, 0.06)`. This mimics soft, overhead gallery lighting.
*   **Ghost Borders:** For input fields where a boundary is required for accessibility, use `outline_variant` (#c1c7d0) at **20% opacity**. It should feel like a suggestion of a box, not a cage.

---

## 5. Components: Precision Primitives

### Action Buttons
*   **Primary:** `primary` (#003354) background with `on_primary` (#ffffff) text. Use `rounded-md` (0.375rem).
*   **Secondary:** `secondary_container` (#a0f399) with `on_secondary_container` (#217128). This provides the "Professional Green" for positive actions like "Finalize Sale."
*   **Tertiary:** No background. `primary` text. Only used for low-priority "Cancel" or "Back" actions.

### Data Visualizations & Badges
*   **Status Badges:** Do not use heavy backgrounds. Use a "Soft Tint" approach: `error_container` (#ffdad6) background with `on_error_container` (#93000a) text for low-stock alerts.
*   **Data Bars:** Use `primary_fixed` (#cfe5ff) as the track and `primary` (#003354) as the fill for a sophisticated, monochromatic look.

### Cards & Lists
*   **Constraint:** Never use a horizontal rule (`<hr>`) to separate list items. 
*   **Solution:** Use `spacing-4` (0.9rem) of vertical white space or a subtle `surface_dim` (#d9dade) background on alternating rows.

### Inventory Inputs
*   **Focus State:** When a user clicks an input, the border does not just change color—the background shifts to `surface_container_lowest` (#ffffff) and the `primary` color creates a 2px bottom-only "underline" to signal active focus.

---

## 6. Do’s and Don’ts

### Do:
*   **Embrace Whitespace:** Use `spacing-10` (2.25rem) between major sections. If it feels like "too much" space, you are likely doing it right.
*   **Asymmetric Layouts:** Place your primary KPI in a large `display-md` font on the left, with supporting metadata in a small `label-md` column on the right. 
*   **Tone-on-Tone:** Use `on_surface_variant` for secondary text to keep the UI from feeling "ink-heavy."

### Don’t:
*   **Don't use 100% Black:** Never use #000000. Use `on_surface` (#191c1f) for the darkest text to maintain the premium, soft-commercial feel.
*   **Don't use Sharp Corners:** Avoid `rounded-none`. Even the most "professional" app needs the approachability of `rounded-sm` (0.125rem).
*   **Don't Over-Iconize:** Use icons sparingly. If a text label is clear, the icon is likely clutter. Only use icons to highlight status (Success/Warning/Error).

### Accessibility Note:
While we utilize soft tonal shifts, ensure that all text-to-background combinations meet WCAG AA standards. When using `surface_container` tiers, always pair with `on_surface` or `primary` for maximum legibility in high-glare retail environments.