package com.daitong.service;


import com.daitong.bo.game.gomoku.Room;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GomokuService {
    private Map<String, Room> rooms = new ConcurrentHashMap<>();

    public String createRoom(String userId) {
        String roomId = UUID.randomUUID().toString();
        String invitationCode = UUID.randomUUID().toString();
        Room room = new Room(roomId, invitationCode);
        room.getPlayerIds().add(userId);
        rooms.put(roomId, room);
        return invitationCode;
    }

    public String joinRoom(String userId, String invitationCode) {
        for (Room room : rooms.values()) {
            if (room.getInvitationCode().equals(invitationCode) && room.getPlayerIds().size() < 2) {
                room.getPlayerIds().add(userId);
                return room.getRoomId();
            }
        }
        return null;
    }

    public Room getRoomStatus(String roomId) {
        return rooms.get(roomId);
    }

    public boolean startGame(String roomId) {
        Room room = rooms.get(roomId);
        if (room != null && room.getPlayerIds().size() == 2 && !room.isGameStarted()) {
            room.setGameStarted(true);
            return true;
        }
        return false;
    }

    public boolean makeMove(String roomId, String userId, int x, int y) {
        Room room = rooms.get(roomId);
        if (room != null && room.isGameStarted()) {
            // 简单检查落子位置是否合法
            if (room.getBoard()[x][y] == 0) {
                int playerIndex = room.getPlayerIds().indexOf(userId);
                room.getBoard()[x][y] = playerIndex + 1;

                // 判断是否有玩家获胜
                if (checkWin(room.getBoard(), x, y, playerIndex + 1)) {
                    room.setHasWinner(true);
                    room.setWinnerId(userId);
                    room.setGameStarted(false);
                }
                return true;
            }
        }
        return false;
    }

    private boolean checkWin(int[][] board, int x, int y, int player) {
        int row = board.length;
        int col = board[0].length;

        // 检查横向
        int count = 1;
        for (int i = y - 1; i >= 0 && board[x][i] == player; i--) {
            count++;
        }
        for (int i = y + 1; i < col && board[x][i] == player; i++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        // 检查纵向
        count = 1;
        for (int i = x - 1; i >= 0 && board[i][y] == player; i--) {
            count++;
        }
        for (int i = x + 1; i < row && board[i][y] == player; i++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        // 检查正斜向
        count = 1;
        for (int i = x - 1, j = y - 1; i >= 0 && j >= 0 && board[i][j] == player; i--, j--) {
            count++;
        }
        for (int i = x + 1, j = y + 1; i < row && j < col && board[i][j] == player; i++, j++) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        // 检查反斜向
        count = 1;
        for (int i = x - 1, j = y + 1; i >= 0 && j < col && board[i][j] == player; i--, j++) {
            count++;
        }
        for (int i = x + 1, j = y - 1; i < row && j >= 0 && board[i][j] == player; i++, j--) {
            count++;
        }
        if (count >= 5) {
            return true;
        }

        return false;
    }

    public boolean endGame(String roomId, String winnerId) {
        Room room = rooms.get(roomId);
        if (room != null && room.isGameStarted()) {
            room.setGameStarted(false);
            return true;
        }
        return false;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }
}