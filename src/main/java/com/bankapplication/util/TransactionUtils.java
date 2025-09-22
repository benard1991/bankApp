package com.bankapplication.service.util;

import com.bankapplication.model.Account;
import com.bankapplication.model.Transaction;
import com.bankapplication.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionUtils {

    private final TransactionRepository transactionRepository;

    public void createTransaction(
            Account account,
            Double amount,
            String transactionType,
            String sourceAccount,
            String  destinationAccount,
            String transferChannel,
            String destinationBank,
            String status
    ) {
        Transaction transaction = new Transaction();

        transaction.setAmount(amount);
        transaction.setTransactionType(transactionType);
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setTransferChannel(transferChannel);
        transaction.setDestinationBank(destinationBank);
        transaction.setStatus(status);

        if (account != null) {
            transaction.setAccount(account);
            transaction.setUser(account.getUser());
        }


        transactionRepository.save(transaction);
    }
}
