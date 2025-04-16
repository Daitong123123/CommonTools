package com.daitong.bo.message;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * FriendShipResponse
 *
 * @since 2025-04-16
 */
@Data
@ToString
public class FriendShipResponse {

    private List<String> friends;
}
