package ru.yandex.practicum.filmorate.storage.userfriend;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFriend;
import ru.yandex.practicum.filmorate.storage.EntityAlreadyExistsException;
import ru.yandex.practicum.filmorate.storage.EntityIsNotFoundException;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class InMemoryUserFriendStorage implements UserFriendStorage {
    private final Map<Long, UserFriend> storage = new HashMap<>();

    private long nextId = 1L;

    static class UserFriendsByUserIdsPredicate implements Predicate<UserFriend> {
        private final Long userIdA;
        private final Long userIdB;

        public UserFriendsByUserIdsPredicate(Long userIdA, Long userIdB) {
            this.userIdA = userIdA;
            this.userIdB = userIdB;
        }

        @Override
        public boolean test(UserFriend userFriend) {
            return Objects.equals(userFriend.getUserIdA(), userIdA) && Objects.equals(userFriend.getUserIdB(), userIdB)
                    || Objects.equals(userFriend.getUserIdA(), userIdB) && Objects.equals(userFriend.getUserIdB(), userIdA);
        }
    }

    @Override
    public List<UserFriend> getAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public UserFriend createByUserIds(Long userIdA, Long userIdB) throws EntityAlreadyExistsException, FriendOfHisOwnException {
        UserFriend userFriendEntity = new UserFriend(nextId++, userIdA, userIdB);
        create(userFriendEntity);

        return userFriendEntity;
    }

    public UserFriend create(UserFriend userFriendEntity) throws EntityAlreadyExistsException, FriendOfHisOwnException {
        if (storage.containsKey(userFriendEntity.getId())) {
            throw new EntityAlreadyExistsException(userFriendEntity);
        }

        if (Objects.equals(userFriendEntity.getUserIdA(), userFriendEntity.getUserIdB())) {
            throw new FriendOfHisOwnException(userFriendEntity);
        }

        if (
                storage.values().stream().anyMatch(
                        new UserFriendsByUserIdsPredicate(userFriendEntity.getUserIdA(), userFriendEntity.getUserIdB())
                )
        ) {
            throw new EntityAlreadyExistsException(userFriendEntity);
        }

        storage.put(userFriendEntity.getId(), userFriendEntity);

        return userFriendEntity;
    }

    @Override
    public void deleteByUserIds(Long userIdA, Long userIdB) throws EntityIsNotFoundException {
        Optional<UserFriend> entityToDelete = storage
                .values()
                .stream()
                .filter(new UserFriendsByUserIdsPredicate(userIdA, userIdB))
                .findFirst();

        if (entityToDelete.isEmpty()) {
            throw new EntityIsNotFoundException(new UserFriend(0L, userIdA, userIdB));
        }

        storage.remove(entityToDelete.get().getId());
    }

    @Override
    public List<Long> getUserIdsInCommon(Long userIdA, Long userIdB) {
        List<Long> friendsA = new LinkedList<>();
        List<Long> friendsB = new LinkedList<>();

        for (UserFriend userFriend : storage.values()) {
            if (Objects.equals(userFriend.getUserIdA(), userIdA)) {
                friendsA.add(userFriend.getUserIdB());
            } else if (Objects.equals(userFriend.getUserIdB(), userIdA)) {
                friendsA.add(userFriend.getUserIdA());
            } else if (Objects.equals(userFriend.getUserIdA(), userIdB)) {
                friendsB.add(userFriend.getUserIdB());
            } else if (Objects.equals(userFriend.getUserIdB(), userIdB)) {
                friendsB.add(userFriend.getUserIdA());
            }
        }

        return friendsA.stream().filter(friendsB::contains).collect(Collectors.toList());
    }
}
