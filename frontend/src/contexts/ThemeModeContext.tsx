import { createContext, useContext, useMemo } from 'react';
import type { Dispatch, ReactNode, SetStateAction } from 'react';
import { usePersistentState } from '../hooks/usePersistentState';
import type { AppThemeMode } from '../theme';

type ThemeModeContextValue = {
  mode: AppThemeMode;
  setMode: Dispatch<SetStateAction<AppThemeMode>>;
  toggleMode: () => void;
};

const ThemeModeContext = createContext<ThemeModeContextValue | undefined>(undefined);

type ThemeModeProviderProps = {
  children: ReactNode;
};

export function ThemeModeProvider({ children }: ThemeModeProviderProps) {
  const [mode, setMode] = usePersistentState<AppThemeMode>('scj-theme-mode', 'light');

  const value = useMemo<ThemeModeContextValue>(() => ({
    mode,
    setMode,
    toggleMode: () => setMode((current) => current === 'light' ? 'dark' : 'light')
  }), [mode, setMode]);

  return <ThemeModeContext.Provider value={value}>{children}</ThemeModeContext.Provider>;
}

export function useThemeMode() {
  const context = useContext(ThemeModeContext);
  if (!context) {
    throw new Error('useThemeMode deve ser usado dentro de ThemeModeProvider');
  }
  return context;
}