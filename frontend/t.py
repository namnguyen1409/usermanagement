import asyncio
import aiohttp
from concurrent.futures import ThreadPoolExecutor
import time

# Cấu hình
URL = "https://backend.telecomic.top/"
TOTAL_REQUESTS = 2000   # Tổng số request bạn muốn gửi
THREADS = 8             # Số thread (có thể tăng lên theo CPU)
REQUESTS_PER_THREAD = TOTAL_REQUESTS // THREADS
CONCURRENT_REQUESTS = 100  # Số lượng concurrent trong mỗi thread (async)


async def send_request(session, i):
    try:
        async with session.get(URL, timeout=5) as response:
            print(f"[{i}] Status: {response.status} - {response.url}")
            return response.status
    except Exception as e:
        print(f"[{i}] Error: {e}")
        return None


async def async_worker(start_index, count):
    connector = aiohttp.TCPConnector(limit=None)
    timeout = aiohttp.ClientTimeout(total=10)

    async with aiohttp.ClientSession(connector=connector, timeout=timeout) as session:
        tasks = [asyncio.create_task(send_request(session, start_index + i)) for i in range(count)]
        await asyncio.gather(*tasks)


def thread_worker(thread_id):
    print(f"🚀 Thread-{thread_id} bắt đầu")
    asyncio.run(async_worker(thread_id * REQUESTS_PER_THREAD, REQUESTS_PER_THREAD))
    print(f"✅ Thread-{thread_id} xong")


def main():
    start = time.time()
    with ThreadPoolExecutor(max_workers=THREADS) as executor:
        for i in range(THREADS):
            executor.submit(thread_worker, i)
    duration = time.time() - start
    print(f"\n🎯 Gửi {TOTAL_REQUESTS} requests xong trong {duration:.2f} giây.")

if __name__ == "__main__":
    main()
