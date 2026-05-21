-- Supabase Setup Script for Signy App --

-- 1. Create Messages Table
CREATE TABLE public.messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    senderId TEXT NOT NULL,
    text TEXT NOT NULL,
    timestamp TIMESTAMPTZ DEFAULT now(),
    type TEXT DEFAULT 'TEXT'
);

-- 2. Create Businesses Table
CREATE TABLE public.businesses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    address TEXT,
    category TEXT
);

-- 3. Create Cameras Table
CREATE TABLE public.cameras (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL,
    url TEXT NOT NULL,
    status TEXT DEFAULT 'ONLINE',
    businessId UUID REFERENCES public.businesses(id)
);

-- 4. Enable Realtime for Messages
ALTER PUBLICATION supabase_realtime ADD TABLE public.messages;

-- 5. Seed Data (Optional)
INSERT INTO public.businesses (name, latitude, longitude, address, category) 
VALUES ('Signy HQ', 30.7333, 76.7794, 'Sector 17, Chandigarh', 'Main Office');

INSERT INTO public.cameras (name, url, businessId) 
VALUES ('Front Entrance', 'rtsp://dvr.local/ch1', (SELECT id FROM public.businesses LIMIT 1));
