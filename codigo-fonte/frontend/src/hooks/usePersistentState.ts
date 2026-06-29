import { useEffect, useState } from 'react';

export function usePersistentState<T>(key: string, initialValue: T) {
  const [value, setValue] = useState<T>(() => {
    if (typeof window === 'undefined') {
      return initialValue;
    }

    const storedValue = window.localStorage.getItem(key);
    if (storedValue === null) {
      return initialValue;
    }

    try {
      const parsed = JSON.parse(storedValue) as T;
      if (Array.isArray(initialValue) && !Array.isArray(parsed)) {
        return initialValue;
      }
      return parsed;
    } catch {
      return initialValue;
    }
  });

  useEffect(() => {
    window.localStorage.setItem(key, JSON.stringify(value));
  }, [key, value]);

  return [value, setValue] as const;
}
