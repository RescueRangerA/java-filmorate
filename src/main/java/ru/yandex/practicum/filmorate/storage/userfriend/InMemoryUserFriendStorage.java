package ru.yandex.practicum.filmorate.storage.userfriend;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;
import ru.yandex.practicum.filmorate.util.Graph;

import java.util.*;

@Component
public class InMemoryUserFriendStorage implements UserFriendStorage {
    private final Graph<Long, UserFriend> storage = new Graph<>();

    @Override
    public List<UserFriend> getAll() {
        return storage.getEdges();
    }

    public List<Long> getUserIdsByUser(User user) {
        return new LinkedList<>(storage.getEdgesOfVertex(user.getId()).keySet());
    }

    @Override
    public UserFriend createByUserIds(User userA, User userB) throws EntityAlreadyExistsException, FriendOfHisOwnException {
        UserFriend userFriendEntity = new UserFriend(userA.getId(), userB.getId());
        create(userFriendEntity);

        return userFriendEntity;
    }

    public UserFriend create(UserFriend userFriendEntity) throws EntityAlreadyExistsException, FriendOfHisOwnException {
        Long userA = userFriendEntity.getFromUserId();
        Long userB = userFriendEntity.getToUserId();

        if (Objects.equals(userA, userB)) {
            throw new FriendOfHisOwnException(userFriendEntity);
        }

        storage.addVertex(userA);
        storage.addVertex(userB);

        if (storage.getEdge(userA, userB) != null) {
            throw new EntityAlreadyExistsException(userFriendEntity);
        }

        UserFriend maybeExistingUserFriendEntity = storage.getEdge(userB, userA);

        if (maybeExistingUserFriendEntity != null) {
            userFriendEntity.setStatus(UserFriend.Status.CONFIRMED);
            maybeExistingUserFriendEntity.setStatus(UserFriend.Status.CONFIRMED);

            storage.addEdge(userA, userB, userFriendEntity);
            storage.addEdge(userB, userA, maybeExistingUserFriendEntity);
        } else {
            userFriendEntity.setStatus(UserFriend.Status.PENDING);
            storage.addEdge(userA, userB, userFriendEntity);
        }

        return userFriendEntity;
    }

    @Override
    public void deleteByUserIds(User userA, User userB) throws EntityIsNotFoundException {
        if (storage.getEdge(userA.getId(), userB.getId()) == null) {
            throw new EntityIsNotFoundException(UserFriend.class, 0L);
        }

        storage.removeEdge(userA.getId(), userB.getId());
    }

    @Override
    public List<Long> getUserIdsInCommon(User userA, User userB) {
        Set<Long> friendsA = storage.getEdgesOfVertex(userA.getId()).keySet();
        Set<Long> friendsB = storage.getEdgesOfVertex(userB.getId()).keySet();
        friendsA.retainAll(friendsB);

        return new LinkedList<>(friendsA);
    }
}
