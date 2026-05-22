import { serve } from "https://deno.land/std@0.168.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

serve(async (req) => {

  try {

    const { email, otp } = await req.json()

    const supabase = createClient(
      "https://gknjnodksyrhmaxzkdrk.supabase.co",
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imdrbmpub2Rrc3lyaG1heHprZHJrIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3OTM1NDE4NywiZXhwIjoyMDk0OTMwMTg3fQ.OBkauf28Vnb6xV0DpYrOtdCYFhNGsxuQBFsGXDuPZNc"
    )

    // Cari OTP terbaru
    const { data, error } = await supabase
      .from("email_otps")
      .select("*")
      .eq("email", email)
      .eq("otp_code", otp)
      .eq("is_verified", false)
      .order("created_at", { ascending: false })
      .limit(1)
      .single()

    if (error || !data) {

      return new Response(
        JSON.stringify({
          success: false,
          message: "OTP tidak valid"
        }),
        {
          headers: {
            "Content-Type": "application/json"
          },
          status: 400
        }
      )
    }

    // Cek expired
    const now = new Date()
    const expired = new Date(data.expires_at)

    if (now > expired) {

      return new Response(
        JSON.stringify({
          success: false,
          message: "OTP sudah kadaluarsa"
        }),
        {
          headers: {
            "Content-Type": "application/json"
          },
          status: 400
        }
      )
    }

    // Update verified
    await supabase
      .from("email_otps")
      .update({
        is_verified: true
      })
      .eq("id", data.id)

    return new Response(
      JSON.stringify({
        success: true,
        message: "OTP berhasil diverifikasi"
      }),
      {
        headers: {
          "Content-Type": "application/json"
        },
        status: 200
      }
    )

  } catch (e) {

    return new Response(
      JSON.stringify({
        success: false,
        error: e.message
      }),
      {
        headers: {
          "Content-Type": "application/json"
        },
        status: 500
      }
    )
  }
})