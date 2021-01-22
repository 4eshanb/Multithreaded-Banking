# Multithreaded-Banking
One thread is used for depositing money. Another is used for withdrawing. We don't want to overdraw from the account, so the withdraw will check if there is enough money, or it will wait. Use Conditions and locks to ensure synchronization and avoid race conditions.

Limited function right now.
