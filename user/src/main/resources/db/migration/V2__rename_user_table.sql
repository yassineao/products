DO $$
BEGIN
    IF to_regclass('public."user"') IS NOT NULL
        AND to_regclass('public.app_user') IS NULL THEN
        ALTER TABLE public."user" RENAME TO app_user;
    END IF;
END
$$;
