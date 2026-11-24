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

package app.krista.extensions.essentials.collaboration.teams;

import app.krista.extension.essentials.collaboration.teams.teams.TeamsCommand;
import app.krista.extension.essentials.collaboration.teams.teams.TeamsMessage;

import static app.krista.extension.essentials.collaboration.teams.util.Constants.GSON_JSON_MAPPER;

public class TeamsPojoGenerator {
    public static TeamsCommand createTeamsCommand(){
        String teamsCommand = """
                {
                  "command": "sampleCommand",
                  "teamsUserProfile": {
                    "id": "user456",
                    "name": "Jane Smith",
                    "emailId": "jane.smith@example.com",
                    "tenantId": "3694f6b4-b5f1-47ef-852f-a0b4a459ab44"
                  }
                }
                """;

        return GSON_JSON_MAPPER.fromString(teamsCommand, TeamsCommand.class);
    }

    public static TeamsMessage createTeamsMessage() {
        String teamsMessage = """
                {
                  "message": "Hello world",
                  "textFormat": "plain",
                  "type": "message",
                  "id": "114149291599657",
                  "channelId": "msteams",
                  "teamsUserProfile": {
                    "id": "user456",
                    "name": "Jane Smith",
                    "emailId": "jane.smith@example.com",
                    "tenantId": "3694f6b4-b5f1-47ef-852f-a0b4a459ab44"
                  }
                }
                """;

        return GSON_JSON_MAPPER.fromString(teamsMessage, TeamsMessage.class);
    }

}
