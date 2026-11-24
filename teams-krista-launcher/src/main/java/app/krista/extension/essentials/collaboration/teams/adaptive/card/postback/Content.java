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

package app.krista.extension.essentials.collaboration.teams.adaptive.card.postback;

import app.krista.omni.sdk.medium.api.dto.MessageDto;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Content {

    private final String title;

    private final List<Button> buttons;

    private Content(String title, List<Button> buttons) {
        this.title = title;
        this.buttons = buttons;
    }

    @SuppressWarnings("unchecked")
    public static Content create(MessageDto messageDto) {
        List<String> options = (List<String>) messageDto.getForm().getPages().getFirst().getFields().getFirst().getValue();

        List<Button> buttons = options.stream().map(option -> new Button(option, option)).collect(Collectors.toList());
        return new Content(messageDto.getMessage(), buttons);
    }

    public String getTitle() {
        return title;
    }

    public List<Button> getButtons() {
        return buttons;
    }
}
