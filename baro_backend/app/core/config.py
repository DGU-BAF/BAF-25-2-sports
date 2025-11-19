# app/config.py
import os
from dotenv import load_dotenv

load_dotenv()

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_ANON_KEY = os.getenv("SUPABASE_ANON_KEY")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

if not SUPABASE_URL or not SUPABASE_ANON_KEY:
    raise RuntimeError("Supabase 환경변수(SUPABASE_URL, SUPABASE_ANON_KEY)를 설정하세요.")

if not OPENAI_API_KEY:
    raise RuntimeError("OPENAI_API_KEY를 설정하세요.")