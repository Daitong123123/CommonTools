package com.daitong.bo.game.gomoku;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private String roomId;
    private String invitationCode;
    private List<String> playerIds;
    private List<Score> scores;
    public String getBlackUserId() {
        return blackUserId;
    }

    public void setBlackUserId(String blackUserId) {
        this.blackUserId = blackUserId;
    }

    private String blackUserId;
    private boolean gameStarted;
    private int[][] board;
    private boolean hasWinner;
    private String winnerId;

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    private String currentUser;

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    private String gameStatus;

    public Room(String roomId, String invitationCode) {
        this.roomId = roomId;
        this.invitationCode = invitationCode;
        this.playerIds = new ArrayList<>();
        this.gameStarted = false;
        this.board = new int[15][15];
        this.hasWinner = false;
        this.winnerId = null;
    }

    // Getters and Setters
    public String getRoomId() {
        return roomId;
    }


    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public List<String> getPlayerIds() {
        return playerIds;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public boolean isHasWinner() {
        return hasWinner;
    }

    public void setHasWinner(boolean hasWinner) {
        this.hasWinner = hasWinner;
    }

    public String getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(String winnerId) {
        this.winnerId = winnerId;
    }
}