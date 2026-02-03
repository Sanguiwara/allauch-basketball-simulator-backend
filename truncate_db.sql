DO $$
DECLARE
  r RECORD;
BEGIN
  -- Change 'public' si besoin
  FOR r IN (
    SELECT tablename
    FROM pg_tables
    WHERE schemaname = 'public'
  ) LOOP
    EXECUTE format('TRUNCATE TABLE %I.%I RESTART IDENTITY CASCADE;', 'public', r.tablename);
  END LOOP;
END $$;
