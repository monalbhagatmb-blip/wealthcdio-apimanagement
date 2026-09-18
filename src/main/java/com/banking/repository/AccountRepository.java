package com.banking.repository;

import com.banking.domain.Account;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {
    private final Map<String, Account> storage = new ConcurrentHashMap<>();

    public void save(Account account) {
        storage.put(account.getId(), account);
    }

    public Optional<Account> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }
}