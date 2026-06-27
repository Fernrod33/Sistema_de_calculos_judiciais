import { Alert, Snackbar } from '@mui/material';
import { createContext, useContext, useMemo, useState, type ReactNode } from 'react';

type Notification = {
  message: string;
  severity: 'success' | 'error' | 'info' | 'warning';
};

type NotificationContextValue = {
  notify: (notification: Notification) => void;
};

const NotificationContext = createContext<NotificationContextValue | undefined>(undefined);

export function NotificationProvider({ children }: { children: ReactNode }) {
  const [notification, setNotification] = useState<Notification | null>(null);

  const value = useMemo<NotificationContextValue>(() => ({
    notify: (nextNotification) => setNotification(nextNotification)
  }), []);

  return (
    <NotificationContext.Provider value={value}>
      {children}
      <Snackbar
        open={Boolean(notification)}
        autoHideDuration={4200}
        onClose={() => setNotification(null)}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
      >
        <Alert severity={notification?.severity ?? 'info'} variant="filled" onClose={() => setNotification(null)}>
          {notification?.message}
        </Alert>
      </Snackbar>
    </NotificationContext.Provider>
  );
}

export function useNotification() {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within NotificationProvider');
  }
  return context;
}