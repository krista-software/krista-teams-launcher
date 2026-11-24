/*
 * Teams Launcher Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extension.essentials.collaboration.teams.adaptive.card.open.link;

import java.util.List;

@SuppressWarnings("unused")
public class ContentWithLink {

    private final String title;

    private final List<ButtonWithLink> buttons;

    private ContentWithLink(String title, List<ButtonWithLink> buttons) {
        this.title = title;
        this.buttons = buttons;
    }

    public static ContentWithLink create(String url, String buttonText, String title) {
        return new ContentWithLink(title, List.of(new ButtonWithLink(buttonText, url)));
    }

    public String getTitle() {
        return title;
    }

    public List<ButtonWithLink> getButtons() {
        return buttons;
    }
}
